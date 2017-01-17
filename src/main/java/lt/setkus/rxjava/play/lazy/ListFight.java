package lt.setkus.rxjava.play.lazy;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author james
 */
public class ListFight {

    List<Integer> theList = new ArrayList<>();

    Observable<Integer> getNumberObservable() {

        // The Observer only wants EVEN numbers in the list
        IntStream.iterate(0, i -> i + 2).limit(50).forEach(i -> theList.add(i));
        System.out.println(">>> Starting size of the list: " + theList.size());

        return Observable.create(emitter -> {
            int index = 0;
            while (index < theList.size()) {
                int number = theList.get(index);
                System.out.format("calling on next with: %d %s %n", number, (number % 2 != 0) ? "huh what's an odd nunber doing in here?" : "");
                emitter.onNext(number);
                index++;
            }
            System.out.println(">>> Final size of the list: " + theList.size());
        });
    }

    Consumer<Integer> anarchyNumberObserver = (Integer i)  -> {
            // The observer takes even numbers, but it doesn't like it :'(
            // For every even number it receives it places an odd number back into the array for the Observable to emit!
            if (i % 2 == 0) {
                int indexOfOddNo = theList.indexOf(i) + 1;
                int oddNumber = i + 1;
                System.out.println("Eugh, received: " + i + " ...ANARCHY adding to list: " + oddNumber + " at pos:" + indexOfOddNo);
                theList.add(indexOfOddNo, oddNumber);
            } else {
                System.out.println("YES I love odd numbers, received " + i);
            }
    };

    Consumer<Integer> happyNumberObserver = (i) -> System.out.println("received: " + i);

    void start() {
        // Try swapping the happyNumberObserver with the anarchyNumberObserver!
        getNumberObservable()
                .subscribe(happyNumberObserver);
    }

    public static void main(String[] args) {
        new ListFight().start();
    }
}
