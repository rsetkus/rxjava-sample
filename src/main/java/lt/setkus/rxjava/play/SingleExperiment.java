package lt.setkus.rxjava.play;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by robertas on 14/12/16.
 */
public class SingleExperiment {

    public static void main(String[] args) {
        Single.fromCallable(() -> new Person("Robertas"))
                .subscribeWith(new ExperimentObserver());
    }

    static class ExperimentObserver implements SingleObserver<Person> {

        @Override
        public void onSubscribe(Disposable d) {
            System.out.println("Subscribed " + d);
        }

        @Override
        public void onSuccess(Person value) {
            System.out.print("Got person who's name is " + value);
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("Houston we have a problem:  " + e.getMessage());
        }
    }
}
