import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.Word2Vec

import scala.io.StdIn

/**
  * Created by the-magical-llamicorn on 4/15/17.
  */
object Main {

  val sc = new SparkContext()
  val wordRegex = "[\\P{L}\\p{N}]+".r
  val word2vec = new Word2Vec

  def main(args: Array[String]): Unit = {
    val input = sc textFile "corpus.txt" map (wordRegex split _) map (_.toIterable)

    word2vec setVectorSize 3
    val model = word2vec fit input
    val vectors = model.getVectors

    while (true) {
      val input = wordRegex split StdIn.readLine("Word: ")
      if (input.length == 1)
        println(s"Color: ${vectors get input(0)}")
    }
  }
}
