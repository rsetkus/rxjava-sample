package james;

import io.reactivex.observables.ConnectableObservable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CreateObservable {

    public static ConnectableObservable<String> from(final InputStream stream) {
        return from(new BufferedReader(new InputStreamReader(stream)));
    }

    public static ConnectableObservable<String> from(final BufferedReader reader) {
        return ConnectableObservable.<String>create(e -> {
            try {
                String line;
                while (!e.isDisposed() && (line = reader.readLine()) != null) {
                    if (line.equals("exit"))
                        break;
                    e.onNext(line);
                }
            } catch (IOException ex) {
                e.onError(ex);
            }

            if (!e.isDisposed())
                e.onComplete();
        }).publish();
    }
}
