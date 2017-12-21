package james;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;

public class CombineSingles {

    public static void main(String[] args) {

        Single<Integer> ints = Single.just(1); // secondary
        Single<Character> chars = Single.just('a'); // trump
        Single<Character> chars2 = Single.just('b'); // third

        Single<String> intsStr = ints
            .map(i -> i.toString());

        Single<String> charStr = chars
            .map(c -> c.toString());

        Single<String> another = chars2
            .map(c -> c.toString());

        List<Single<String>> list = new ArrayList<>();
        list.add(another);
        list.add(intsStr);
        list.add(charStr);

        Single.merge(list)
            .toObservable()
            .onErrorReturnItem("empty")
            .takeUntil(CombineSingles::shortCircuit)
            .subscribe(new StrSubscriber());
    }

    private static boolean shortCircuit(String s) {
        return s.equals("a");
    }

    private static class StrSubscriber implements Observer<String> {

        final List<String> itemsReceived = new ArrayList<>();

        @Override
        public void onError(Throwable t) {
            System.out.println("onError. Should never happen.");
        }

        @Override
        public void onSubscribe(Disposable d) {
            System.out.println("subscribed");
        }

        @Override
        public void onComplete() {
            if (itemsReceived.contains("a")) {
                System.out.println("Got an a (trump)");
            } else if (itemsReceived.contains("1")) {
                System.out.println("Got an 1 (secondary)");
            } else {
                System.out.println("Got nothing worth showing");
            }
        }

        @Override
        public void onNext(String s) {
            System.out.println("onNext got:" + s);
            itemsReceived.add(s);
        }
    }
}
