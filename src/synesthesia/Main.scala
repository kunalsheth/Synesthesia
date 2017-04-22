package synesthesia

  import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language
  import de.tudarmstadt.ukp.wikipedia.api.{DatabaseConfiguration, Wikipedia}
  import org.apache.spark.mllib.feature.Word2Vec
  import org.apache.spark.{SparkConf, SparkContext}

  import scala.io.StdIn
  import scala.util.Try

/**
  * Created by the-magical-llamicorn on 4/15/17.
  */
object Main {

  val wordRegex = "[\\P{L}\\p{N}]+".r

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf
    conf.setAppName("Synesthesia")
    conf.setMaster(args(0))
    val sc = new SparkContext(conf)

    val stdIn = StdIn

    val dbConfig = new DatabaseConfiguration
    dbConfig setHost "10.0.0.4"
    dbConfig setDatabase "jwpl_tables"
    dbConfig setUser "jwpl"
    dbConfig setPassword ""
    dbConfig setLanguage Language.english

    val wikipedia = new Wikipedia(dbConfig)
    val pageIterator = wikipedia.getPages

    import scala.collection.JavaConverters._
    val wikiRdd = sc parallelize (pageIterator.asScala toSeq)
      .map(p => Try(p getPlainText))
      .filter(_.isSuccess)
      .map(_.get)
      .map(wordRegex split _)
      .map(_.toIterable)

    val word2vec = new Word2Vec
    word2vec setVectorSize 3
    val model = word2vec fit wikiRdd
    model.save(sc, "word2vec_model.mllib")

    while (true) {
      val input = wordRegex split StdIn.readLine("Word: ")
      if (input.length == 1)
        println(s"Color: ${model transform input(0)}")
    }
  }
}
