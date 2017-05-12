package james;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CombineThings {

    public static void main(String[] args) {
        Observable<Long> seconds = Observable.interval(1, TimeUnit.SECONDS);

        Observable<String> randChar = Observable.fromCallable(() -> {
            Random r = new Random();
            return new Character((char) (r.nextInt(26) + 'a')).toString();
        });

        seconds
                .flatMap(second -> randChar.zipWith(Observable.just(second), (String s, Long l) -> s + "[" + l + "]" ))
                .subscribe(getObserver());

        /**
         * ZIP - exactly like ZipWith terminates too early

        Observable.zip(seconds, randChar, (Long l, String s) -> s + ":" + l)
        .subscribe(getObserver());
         */

        /* ZIP WITH
       seconds
                 .zipWith(randChar, (Long l, String s) -> s + ":" + l) <- terminates too early. See docs
                .map(l -> randChar.)
                .subscribe(getObserver());*/

        /*

        /**
         * Example from docs

        Observable
                .range(1, 5)
                .doOnComplete(() -> System.out.println("A"))
                .zipWith(
                        Observable.range(6, 5)
                                .doOnComplete(() -> System.out.println("B"))
                        , (a, b) -> a + "=" + b)
                .subscribe(System.out::println);
                */

        while (true) {

        }
    }


    static DisposableObserver<Object> getObserver() {

        return new DisposableObserver<Object>() {

            @Override
            public void onNext(Object value) {
                System.out.println("onNext, val= " + value);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError, e" + e);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

    }
}
