/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.setkus.rxjava.play.lazy;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

/**
 *
 * @author james
 */
public class ConcurrentQueueFight {
       Deque<Integer> theList = new LinkedBlockingDeque<>();

    Observable<Integer> getNumberObservable() {

        // The Observer only wants EVEN numbers in the list
        IntStream.iterate(0, i -> i + 2)
                .limit(50)
                .forEach(theList::addLast);

        System.out.println(">>> Starting size of the list: " + theList.size());

        return Observable.create(emitter -> {
            int index = 0;

            for (int number : theList) {
                System.out.format("calling on next with: %d %s %n", number, (number % 2 != 0) ? "huh what's an odd nunber doing in here?" : "");
                emitter.onNext(number);
            }
            System.out.println(">>> Final size of the list: " + theList.size());
        });
    }

    Consumer<Integer> anarchyNumberObserver = (Integer i) -> {
        // The observer takes even numbers, but it doesn't like it :'(
        // For every even number it receives it places an odd number back into the array for the Observable to emit!
        if (i % 2 == 0) {
            int oddNumber = i + 1;
            System.out.println("Eugh, received: " + i + " ...ANARCHY adding to list: " + oddNumber); // + " at pos:" + indexOfOddNo);
            theList.addLast(oddNumber);
        } else {
            System.out.println("YES I love odd numbers, received " + i);
        }
    };

    Consumer<Integer> happyNumberObserver = (i) -> System.out.println("received: " + i);

    void start() {
        // Try swapping the happyNumberObserver with the anarchyNumberObserver!
        getNumberObservable()
                .subscribe(anarchyNumberObserver);
    }

    public static void main(String[] args) {
        new ListFight().start();
    }

}
