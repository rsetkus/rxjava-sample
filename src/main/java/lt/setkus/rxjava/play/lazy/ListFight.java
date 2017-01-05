
package lt.setkus.rxjava.play.lazy;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

/**
 *
 * @author james
 */
public class ListFight {
    
    List<Integer> theList;
    
    Observable<Integer> getNumberObservable() {
        
        return Observable.create(emitter -> {
            int index = 0;
            while (index < theList.size()) {
                int number = theList.get(index);
                System.out.println("calling on next with: " + number);
                emitter.onNext(number);
                index++;
            } 
            System.out.println("Final size of the list: " + theList.size());
        });
    }

    Observer<Integer> getNumberObserver() {
        return new Observer<Integer>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable dspsbl) {
                this.disposable = dspsbl;
            }

            @Override
            public void onNext(Integer i) {
                if (i % 2 == 0) {
                    int indexOfOddNo = theList.indexOf(i) + 1;
                    int oddNumber = i + 1;
                    System.out.println("ANARCHY adding to list: " + oddNumber + " at pos:" + indexOfOddNo);
                    theList.add(indexOfOddNo, oddNumber);
                } else {
                    System.out.println("YES I love odd numbers, received " + i);
                }
            }

            @Override
            public void onError(Throwable thrwbl) {
                System.err.println(thrwbl);
            }

            @Override
            public void onComplete() {
                System.out.println("DONE!");
            }

        };
    }

    void initListAndStart() {
        theList = new CopyOnWriteArrayList<>();
        
        IntStream.iterate(0, i -> i +2).limit(50).forEach(i -> theList.add(i));
        System.out.println("Starting size of the list: " + theList.size());
        getNumberObservable()
                .subscribe(getNumberObserver());
    }
    
    public static void main(String[] args) {
        ListFight fight = new ListFight();
        fight.initListAndStart();
    }
}
