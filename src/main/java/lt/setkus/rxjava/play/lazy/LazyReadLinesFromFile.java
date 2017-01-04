package lt.setkus.rxjava.play.lazy;

import io.reactivex.Observable;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LazyReadLinesFromFile {

    static Observable<String> getLinesObservable(Path path) {
        return Observable.create(emitter -> {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    emitter.onNext(line);
                }
                emitter.onComplete();
            } catch (IOException ex) {
                emitter.onError(ex);
            }
        });
    }

    public static void main(String[] args) {
        Path fourHourseMen = Paths.get("src/main/resources/FourHorsemenOfTheApocalypse.txt");
        getLinesObservable(fourHourseMen)
                .subscribe((String line) -> {
                    System.out.println("line received: " + line);
                    System.out.println("Sleeping...zzzz ");
                    try {
                        Thread.sleep(2000);
                        System.out.println("ok, I'm awake... lets consume more!");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                })
                .dispose();
    }
}
