package lt.setkus.rxjava.play;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by robertas on 18/04/17.
 */
public class CombineSingle {

    static int sequenceNumber = 0;

    public static Account getAccount() {
        List<Subscription> subscriptions = Collections.nCopies(5, new Subscription("370622068656"));
        Account account = new Account();
        account.subscriptions = subscriptions;
        return account;
    }

    public static void main(String[] args) {
        Single.fromCallable(() -> getAccount())                                         // 1) make an account request
                .flatMap(account -> Single.fromCallable(account::getSubscriptions))     // 2) retrieve only subscriptions list because we only need it
                .toObservable()                                                         // 3) dealing with Single<List<Subscription>> need convert to Observable<List<Subscription>> because we need operate with item separately
                .flatMapIterable(subscriptions -> subscriptions)                        // 4) from Observable<List<Subscription>> to Observable<Subscription>
                .flatMap(subscription -> loadFullSubscription(subscription))            // 5) make a FullSubscription request
                .filter(fullSubscription -> fullSubscription.sequenceNumber % 2 == 0)   // 7) back to Single<List<FullSubscription>>
                .subscribeWith(new Observer<FullSubscription>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        d.dispose();
                    }

                    @Override
                    public void onNext(FullSubscription value) {
                        System.out.println(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Completed");
                    }
                });
    }

    public static Observable<FullSubscription> loadFullSubscription(Subscription subscription) {
        return  Observable.create(emitter -> {
            emitter.onNext(new FullSubscription("Robertas", subscription.number, ++sequenceNumber));
            emitter.onComplete();

        });
    };

    static class Account {

        List<Subscription> subscriptions = Collections.emptyList();

        public List<Subscription> getSubscriptions() {
            return subscriptions;
        }
        @Override
        public String toString() {
            return "Account{" +
                    "subscriptions=" + subscriptions +
                    '}';
        }

    }
    static class Subscription {

        String number;

        public Subscription(String name) {
            this.number = name;
        }
        @Override
        public String toString() {
            return "Subscription{" +
                    "number='" + number + '\'' +
                    '}';
        }

    }
    static class FullSubscription {
        String name;
        String number;

        int sequenceNumber;

        public FullSubscription(String name, String number, int sequenceNumber) throws InterruptedException {
            this.name = name;
            this.number = number;
            this.sequenceNumber = sequenceNumber;
            Thread.sleep(1000);
        }
        @Override
        public String toString() {
            return "FullSubscription{" +
                    "name='" + name + '\'' +
                    ", number='" + number + '\'' +
                    ", sequenceNumber=" + sequenceNumber +
                    '}';
        }

    }
}
