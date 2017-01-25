package otherthreads;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author James
 */
public class ScheduleOnVsObserverOn {

    AtomicBoolean generateNumbers = new AtomicBoolean(true);
    ThreadFactory observerOnThreadFactory = new SimpleThreadFactory("observerOn-");
    ThreadFactory subscribeOnThreadFactory = new SimpleThreadFactory("subscribeOn-");

    Observable<Integer> getRandyNumbersObservable() {
        return Observable.create(emitter -> {
            while (generateNumbers.get()) {

                ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
                Integer randomNo = randomGenerator.nextInt(0, 501);
                log("generating number..." + randomNo);
                emitter.onNext(randomNo);

                log("sleeping... zzz");
                Thread.sleep(2000);
            }
        });
    }

    public static void main(String[] args) {
        new ScheduleOnVsObserverOn().run();
    }

    public void run() {
        log("Starting up!");

        getRandyNumbersObservable()
                .subscribeOn(newScheduler("subscribeOn-"))
                .observeOn(newScheduler("observerOn-")) // placement matters, if before the filter, the filter is done on the observerOn scheduler
                .filter(num -> {
                    log("filtering, is " + num + " a prime number?"); 
                    boolean prime = true;
                    for (int i = 2; i < num; i++) {
                        if (num % i == 0) {
                            prime = false;
                            break;
                        }
                    }
                    log("filter complete - is number prime? " + prime);
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
