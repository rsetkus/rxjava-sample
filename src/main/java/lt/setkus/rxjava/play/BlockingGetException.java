package lt.setkus.rxjava.play;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

public class BlockingGetException {

    public static void main(String[] args) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        BlockingGetException blockingGetException = new BlockingGetException(new RuntimeException("Error!"));
        blockingGetException.buildNoOnErrorPushObservable().subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("Subscribed");
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(Integer value) {
                System.out.println("Received value " + value);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Error: " + e.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
            }
        });

        compositeDisposable.clear();
    }

    private final RuntimeException error;

    public BlockingGetException(RuntimeException error) {
        this.error = error;
    }

    Observable<Integer> buildNoOnErrorPushObservable() {
        return Observable.create(e -> {
            List<Integer> integers = getInput().blockingGet();
            getInput().blockingGet();
            for (Integer i : integers) {
                e.onNext(i);
            }
            e.onComplete();
            // NO onError call!!!
        });
    }

    Observable<Integer> buildWithErrorPushObservable() {
        return Observable.create(e -> {
            try {
                List<Integer> integers = getInput().blockingGet();
                for (Integer i : integers) {
                    e.onNext(i);
                }
                e.onComplete();
            } catch (Exception ex) {
                e.onError(new RuntimeException("Caught an error with message: " + ex.getMessage()));
            }
        });
    }

    private Single<List<Integer>> getInput() {
        return Single.create(e -> {
            Thread.sleep(1000);
            throw error;
        });
    }
}
