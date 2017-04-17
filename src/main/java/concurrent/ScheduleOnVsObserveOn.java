package concurrent;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://twitter.com/JakeWharton/status/850039445411725316
 *
 * This is a really good test of whether your mental model for Rx is correct.
 *
 * It's simple: subscribe() goes up, observing down, unsubscribe up.
 *
 *  disposable = observable
 *              .doOnSubscribe( // io  )
 *              .subscribeOn(io())
 *              .unsubscribeOn(io())
 *              .doFinally( // io )
 *              .doOnSubscribe( // main )
 *              .subscribeOn(mainThread())
 *              .unsubscribeOn(mainThread())
 *              .observeOn(mainThread())
 *              .doFinally( // main )
 *              .subscribe();
 *
 *  disposable.dispose();
 *
 * @author James
 */
public class ScheduleOnVsObserveOn {


    Observable<Integer> getRandyNumbersObservable() {
        return Observable.generate(emitter -> {  
                int randomNo = ThreadLocalRandom.current().nextInt(0, 501);
                log("generating number..." + randomNo);
                emitter.onNext(randomNo);
                Thread.sleep(2000);
        });
    }

    public static void main(String[] args) {
        new ScheduleOnVsObserveOn().run();
    }

    public void run() {
        log("Starting up!");

        getRandyNumbersObservable()
                .map(i -> {
                    log("map 1...");
                    return i;
                })
                .subscribeOn(newScheduler("subscribeOn-")) // subscribeOn placement in pipeline does not matter... subscribeOn = where source elements are omitted from.
                .map(i -> {
                    log("map 2...");
                    return i;
                })
                .observeOn(newScheduler("observerOn-1-"))
                .map(i -> {
                    log("map 3...");
                    return i;
                })
                .observeOn(newScheduler("observerOn-2-"))
                .map(i -> {
                    log("map 4...");
                    return i;
                })
                .map(i -> {
                    log("map 5...");
                    return i;
                })
                .observeOn(newScheduler("observerOn-3-")) // placement matters, all functions and downstream consumers will run on this scheduler (unless changed) 
                .filter(num -> {
                    log("filtering, is " + num + " a prime number?");

                    boolean prime = true;
                    for (int i = 2; i < num; i++) {
                        if (num % i == 0) {
                            prime = false;
                            break;
                        }
                    }
                    log("filter - is number prime? " + prime);
                    return prime;
                })
                .subscribe(num -> log("Subscriber has got: " + num));
    }

    Scheduler newScheduler(String threadName) {
        return Schedulers.from(Executors.newCachedThreadPool(new SimpleThreadFactory(threadName)));
    }

    class SimpleThreadFactory implements ThreadFactory {

        String threadName;
        AtomicInteger threadCount = new AtomicInteger(0);

        public SimpleThreadFactory(String threadNameIn) {
            this.threadName = threadNameIn;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, threadName + threadCount.getAndIncrement());
        }
    }

    private void log(String msg) {
        System.out.printf("[%s] %s%n", Thread.currentThread().getName(), msg);
    }
}
