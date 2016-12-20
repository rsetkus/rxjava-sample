package lt.setkus.rxjava.play;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author James
 */
public class FlatMapVsMap {

    static Observable<List<Person>> getPeoples() {
        List<Person> devs = new ArrayList<>();
        devs.add(new Person("Steve"));
        devs.add(new Person("James"));
        devs.add(new Person("Radu"));
        devs.add(new Person("Robertas"));
        devs.add(new Person("Oleg"));
        devs.add(new Person("Nadezhda"));

        List<Person> testers = new ArrayList<>();
        testers.add(new Person("Kyrstina"));
        testers.add(new Person("Iryna"));
        testers.add(new Person("Nadezhda no 2"));

        return Observable.fromArray(devs, testers);
    }

    public static void main(String[] args) {

        System.out.println("\nUsing map - map returns an object of type T");
        Disposable mapSub = getPeoples().map(x -> x).subscribe(System.out::println);

        System.out.println("\nUsing flatmap, returns an Observable<T>, so you need to return an Observable.");
        Disposable flatMapSub = getPeoples().flatMap(x -> Observable.fromIterable(x)).subscribe(System.out::println);

        System.out.println("\nUsing concatMap, returns an ObservableSource<T>, so you need to return an ObservableSource.");
        Disposable concatMapSub = getPeoples().concatMap(x -> Observable.fromIterable(x)).subscribe(System.out::println);
        
        mapSub.dispose();
        flatMapSub.dispose();
        concatMapSub.dispose();
    }

}
