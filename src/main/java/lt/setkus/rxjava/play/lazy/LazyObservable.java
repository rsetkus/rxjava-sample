package lt.setkus.rxjava.play.lazy;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LazyObservable {

    static Observable<Integer> getRandomNumberObservable() {
        return Observable.create(emitter -> {
            while (true) {
                int randy = ThreadLocalRandom.current().nextInt(100);
                System.out.println("GENERATED A NEW RANDOM NUMBER " + randy);
                emitter.onNext(randy);
            }
        });
    }

    static Observer<Integer> getRandomNumberObserver() {
        return new Observer<Integer>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable dspsbl) {
                this.disposable = dspsbl;
            }

            @Override
            public void onNext(Integer i) {
                System.out.println("onNext received: " + i + " Sleeping...zzzz ");
                try {
                    Thread.sleep(5000);
                    System.out.println("ok, I'm awake... lets consume more!");
                } catch (InterruptedException ex) {
                    Logger.getLogger(LazyObservable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onError(Throwable thrwbl) {
                System.err.println(thrwbl);
            }

            @Override
            public void onComplete() {
                System.out.println("DONE!");
            }

        };
    }

    public static void main(String[] args) {
        getRandomNumberObservable()
                .filter(i -> i % 2 == 0)
                .map(i -> i*2)
                .subscribe(getRandomNumberObserver());
    }
}
