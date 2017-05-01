import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Synesthesia {

    public static final String saveFile = "word2vec.dl4j";

    public static void main(final String[] args) throws IOException {
        final String mode = args[0];

        final Word2Vec word2Vec;
        if (mode.equals("new")) {
            final SentenceIterator sentenceIterator = new FileSentenceIterator(
                    String::toLowerCase,
                    new File(args[1])
            );

            final TokenizerFactory t = new DefaultTokenizerFactory();
            t.setTokenPreProcessor(new CommonPreprocessor());

            word2Vec = new Word2Vec.Builder()
                    .layerSize(3)
                    .minWordFrequency(7)
                    .seed(System.currentTimeMillis())
                    .iterate(sentenceIterator)
                    .tokenizerFactory(t)
                    .build();

            System.out.println("Fitting Word2Vec Model");
            word2Vec.fit();
            System.out.println("Done Fitting Word2Vec Model");

            WordVectorSerializer.writeWord2VecModel(word2Vec, saveFile);
        } else if (mode.equals("load")) word2Vec = WordVectorSerializer.readWord2VecModel(saveFile);
        else {
            word2Vec = null;
            System.exit(1);
        }

        // TODO: 4/30/17 Make Colors
    }

    protected static double[] normalize(final double[] vector) {
        final double magnitude = Math.sqrt(
                Arrays.stream(vector)
                        .map(x -> x * x)
                        .sum()
        );
        return Arrays.stream(vector)
                .map(x -> x / magnitude)
                .toArray();
    }
}