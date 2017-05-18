package james;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

public class Replay {

    public static void main(String[] args) {

        Observable<Integer> source = Observable.just(1, 2, 3, 4, 5);
        ReplaySubject<Integer> replay = ReplaySubject.create();

        source.subscribe(replay);

        replay.subscribe(System.out::println);

        replay.subscribe(System.out::println);


    }
}
