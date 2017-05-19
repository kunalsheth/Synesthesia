import java.awt.Color

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory

import scala.io.StdIn
import scala.reflect.io.File

/**
  * Created by the-magical-llamicorn on 5/18/17.
  */
object Synesthesia extends App {

  val saveFile = File("word2vec.dl4j")
  val word2vec = args(0) match {
    case "new" => newWord2Vec()
    case "load" => loadWord2Vec(saveFile)
    case _ => None
  }
  word2vec.foreach(saveWord2Vec(_, saveFile))
  word2vec.foreach(readUserInput)

  def readUserInput(word2vec: Word2Vec): Unit = {
    val word = StdIn.readLine("word: ")
    if (word2vec.hasWord(word)) println(s"#${Integer.toHexString(vecToColor(word2vec.getWordVector(word)).getRGB)}")
    readUserInput(word2vec)
  }

  def vecToColor(vector: Array[Double]): Color = {
    val theta01 = ((Math.atan2(vector(0), vector(1)) + Math.PI) / (2 * Math.PI)).toFloat
    val theta02 = ((Math.atan2(vector(0), vector(2)) + Math.PI) / (2 * Math.PI)).toFloat
    val theta12 = ((Math.atan2(vector(1), vector(2)) + Math.PI) / (2 * Math.PI)).toFloat
    new Color(theta01, theta02, theta12)
  }

  //    protected static Color vecToColor(final double[] vector) {
  ////        final double magnitude = Math.sqrt(
  ////                Arrays.stream(vector)
  ////                        .map(x -> x * x)
  ////                        .sum()
  ////        );
  //
  ////        final int[] rgb = Arrays.stream(vector)
  ////                .map(x -> x / magnitude)
  ////                .peek(x -> System.out.print(x + " "))
  ////                .map(x -> (x + 1) / 2)
  ////                .mapToInt(x -> (int) (x * 255))
  ////                .toArray();
  //
  ////        final double[] hsv = Arrays.stream(vector)
  ////                .map(x -> x / magnitude)
  ////                .peek(x -> System.out.print(x + " "))
  ////                .map(x -> (x + 1) / 2)
  ////                .toArray();
  //
  //        final float theta01 = (float) ((Math.atan2(vector[0], vector[1]) + Math.PI) / (2 * Math.PI));
  //        final float theta02 = (float) ((Math.atan2(vector[0], vector[2]) + Math.PI) / (2 * Math.PI));
  //        final float theta12 = (float) ((Math.atan2(vector[1], vector[2]) + Math.PI) / (2 * Math.PI));
  //
  //        return new Color(theta01, theta02, theta12);
  //    }

  def saveWord2Vec(word2Vec: Word2Vec, file: File): Unit = {
    WordVectorSerializer.writeWord2VecModel(word2Vec, saveFile.jfile)
  }

  def loadWord2Vec(file: File): Some[Word2Vec] = {
    Some(WordVectorSerializer.readWord2VecModel(file.jfile))
  }

  def newWord2Vec(): Some[Word2Vec] = {
    val sentenceIterator = new FileSentenceIterator(
      _.toLowerCase,
      File(args(1)).jfile
    )

    val tokenizerFactory = new DefaultTokenizerFactory()
    tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor())

    val word2Vec = Some(new Word2Vec.Builder()
      .layerSize(3)
      .minWordFrequency(7)
      .seed(System.currentTimeMillis())
      .iterate(sentenceIterator)
      .tokenizerFactory(tokenizerFactory)
      .build())

    println("Fitting Word2Vec Model")
    word2Vec.foreach(_.fit)
    println("Done Fitting Word2Vec Model")

    word2Vec
  }
}
