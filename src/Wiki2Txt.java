import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

/**
 * Created by the-magical-llamicorn on 4/30/17.
 */
public class Wiki2Txt {

    public static void main(final String[] args) throws WikiInitializationException, IOException {
        final DatabaseConfiguration conf = new DatabaseConfiguration();
        conf.setHost(System.console().readLine("host: "));
        conf.setUser(System.console().readLine("user: "));
        conf.setPassword(new String(System.console().readPassword("password: ")));
        conf.setDatabase(System.console().readLine("database: "));
        conf.setLanguage(WikiConstants.Language.english);

        final Wikipedia wikipedia = new Wikipedia(conf);

        final PrintStream output = new PrintStream(new GZIPOutputStream(new FileOutputStream("corpus.txt.gz"), 10_000_000));

        long pages = 0;
        for (final Iterator<Page> i = wikipedia.getPages().iterator(); i.hasNext(); ) {
            Arrays.stream(safeGetPlainText(i.next())
                    .replaceAll("\\s+", " ")
                    .split("[.!?]"))
                    .parallel()
                    .filter(s -> count(' ', s) > 3)
                    .forEach(output::println);
            if (++pages % 1000 == 0) System.out.println(pages + " pages");
        }

        output.flush();
        output.close();
    }

    protected static String safeGetPlainText(final Page page) {
        try {
            return page.getPlainText();
        } catch (final Exception e) {
            System.err.println(e.getMessage());
            return "";
        }
    }

    protected static int count(final char character, final String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == character) count++;
        return count;
    }
}
