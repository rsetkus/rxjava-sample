package james;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CompositeDisposableExperiment {

    AtomicInteger ai = new AtomicInteger(0);

    public static void main(String[] args) {
        new CompositeDisposableExperiment().start();
    }

    void start() {
        Observable<Long> interval = Observable.interval(1, TimeUnit.SECONDS);

        CompositeDisposable disposable = new CompositeDisposable();

        DisposableObserver<Long> ob1 = getObserver();
        disposable.add(ob1);

        interval.subscribe(ob1);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        disposable.clear();

        DisposableObserver<Long> ob2 = getObserver();
        disposable.add(ob2);

        interval.subscribe(ob2);

        while(true) {

        }
    }

    DisposableObserver<Long> getObserver() {
        final int i = ai.incrementAndGet();

        return new DisposableObserver<Long>() {

            @Override
            public void onNext(Long value) {
                System.out.println(i + "-onNext, val" + value);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(i + "-onError, e" + e);
            }

            @Override
            public void onComplete() {
                System.out.println(i + "-onComplete");
            }
        };

    }


}
