package james;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.Random;

public class SingleJuggling {

    public static void main(String[] args) {
        Single<Long> currentMillis = Single.fromCallable(() -> {
            System.out.println("currentMillis on Thread " + Thread.currentThread().getName());
            Thread.sleep(1000);
            return System.currentTimeMillis();
        }).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single());

        Single<String> randChar = Single.fromCallable(() -> {
            System.out.println("randChar on Thread " + Thread.currentThread().getName());
            Random r = new Random();
            return new Character((char) (r.nextInt(26) + 'a')).toString();
        }).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single());

        Single<Integer> randInt = Single.fromCallable(() -> {
            Random r = new Random();
            return r.nextInt(100);
        }).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single());

        currentMillis
            .doOnEvent((l, ex) -> {
                        System.out.println("Got long" + l);
                        if (l != 0) {
                            randInt
                                .subscribe((i) -> System.out.println("Got randy int " + i));
                        }
            })
            .zipWith(randChar,
                (l, ch) -> {
                System.out.println("subscribing on " + Thread.currentThread().getName());
                String x = l + ":" + ch;
                System.out.println(x);
                return x;
            }).subscribe(s -> System.out.println("Final value" + s));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
