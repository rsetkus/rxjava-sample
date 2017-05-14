package james;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import java.util.function.Consumer;

public class FlowableCreateBackPressure {
    public static void main(String[] args) {

        flowableStrings()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe(getDisposableSubscriber());

        while(true) {
        }

        /**
         * BackpressureStrategy.MISSING:
         *
         * RxComputationThreadPool-1: onNext, val= BOB0
         * onError, eio.reactivex.exceptions.MissingBackpressureException: Queue is full?!
         *
         * -------------------------------------
         * BackpressureStrategy.DROP / Latest (similar but you get the latest form the potential dropped ones)
         * RxComputationThreadPool-1: onNext, val= BOB126
         * RxComputationThreadPool-1: onNext, val= BOB127
         * RxComputationThreadPool-1: onNext, val= BOB51368383
         * RxComputationThreadPool-1: onNext, val= BOB51368384
         *
         * -------------------------------------
         * BackpressureStrategy.ERROR
         * RxComputationThreadPool-1: onNext, val= BOB0
         * onError, eio.reactivex.exceptions.MissingBackpressureException: create: could not emit value due to lack of requests
         *
         * -------------------------------------
         * The Flowable hosts the default buffer size of 128 elements for operators, accessible via bufferSize(),
         *
         * BackpressureStrategy.BUFFER
         *
         * RxComputationThreadPool-1: onNext, val= BOB146
         * RxComputationThreadPool-1: onNext, val= BOB147
         * RxComputationThreadPool-1: onNext, val= BOB148
         * RxComputationThreadPool-1: onNext, val= BOB149
         *
         * ^ then it just stops
         */
    }

    static DisposableSubscriber<Object> getDisposableSubscriber() {

        return new DisposableSubscriber<Object>() {

            @Override
            public void onNext(Object value) {
                System.out.println(Thread.currentThread().getName() + ": onNext, val= " + value);
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    static Flowable<Object> flowableStrings() {
        return Flowable.create(e -> {
            new BobEmitter((val) -> e.onNext(val)).start();
        }, BackpressureStrategy.ERROR);
    }


    static class BobEmitter {
        Consumer<String> bobCallback;
        Long l = 0L;

        public BobEmitter(Consumer<String> bobCallback) {
            this.bobCallback = bobCallback;
        }

        public void start() {
            while (true) {
                bobCallback.accept("BOB" + l++);
            }
        }
    }

}
