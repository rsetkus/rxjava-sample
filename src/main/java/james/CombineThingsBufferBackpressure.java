package james;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static io.reactivex.Flowable.just;

public class CombineThingsBufferBackpressure {

        public static void main(String[] args) {
            // make a HOT observable emit every 300 microseconds...
            ConnectableFlowable<Long> hotInterval = Flowable
                .interval(300, TimeUnit.MICROSECONDS)
             //some debug:
            .map(l -> {
                System.out.println("bef:" + l); //if this line outputs it is because the value was created
                return l;
            })
             .onBackpressureBuffer(50, // 50 things in the buffer
                 () -> System.out.println("Crap! Buffer overflow detected, dropping oldest"), // thing to do on overflow
                 BackpressureOverflowStrategy.DROP_OLDEST) // drop the oldest shit in the buffer

           /* some debug:
            .map(l -> {
                System.out.println("aft:" + l); // if this line outputs its because it survived the backpressure cull
                return l;
            })*/
                .publish();
            hotInterval.connect();

            // lookup of random character with 1 milli sec latency
            Flowable<String> randChar = Flowable.fromCallable(() -> {
                Thread.sleep(1);
                Random r = new Random();
                return new Character((char) (r.nextInt(26) + 'a')).toString();
            }).onBackpressureDrop((s) -> System.out.println(Thread.currentThread().getName() + ": rand char dropping" + s));
            // we never see this backpressure at this flowable


            hotInterval
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap(value -> randChar.zipWith(just(value), (String s, Long l) -> s + "[" + l + "]"))
                .subscribe(getDisposableSubscriber());

            while (true) {
            }
        }

        static DisposableSubscriber<Object> getDisposableSubscriber() {

            return new DisposableSubscriber<Object>() {

                @Override
                public void onNext(Object value) {
                    System.out.println(Thread.currentThread().getName() + ": onNext, val= " + value);
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

