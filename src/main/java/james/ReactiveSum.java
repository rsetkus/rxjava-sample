package james;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReactiveSum {

    static ConnectableObservable<String> systemInObservable = CreateObservable.from(System.in);

    public static void main(String[] args) {

        System.out.println("Reactive Sum. Type 'a: <number>' and 'b: <number>' to try it.");

        Observable<Double> a = filteredDoubleStream("a", systemInObservable);
        Observable<Double> b = filteredDoubleStream("b", systemInObservable);

        Observable
            .combineLatest(a, b, (x, y) -> x + y)
            .subscribe(sum -> System.out.println("update : a + b = " + sum));

        systemInObservable.connect();
    }

    public static Observable<Double> filteredDoubleStream(String filter, Observable<String> input) {
        return input
            .map(matches(filter))
            .filter(str -> !"".equals(str))
            .map(Double::parseDouble);
    }

    static Function<String, String> matches(String matchStr) {
        return (String in) -> {
            final Matcher matcher = Pattern.compile("\\s*" + matchStr + "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)$").matcher(in);
            return (matcher.matches() && matcher.group(1) != null) ? matcher.group(1) : "";
        };
    }
}
