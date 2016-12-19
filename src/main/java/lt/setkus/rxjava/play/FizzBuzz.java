package lt.setkus.rxjava.play;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * "Write a program that prints the numbers from 1 to 100. But for multiples of three print “Fizz” instead of the number and for the
 * multiples of five print “Buzz”. For numbers which are multiples of both three and five print “FizzBuzz”."
 */
public class FizzBuzz {

    public static void main(String[] args) {
        Disposable subscribe = Observable.<Integer>range(0, 101)
                .map(fizzBuzzer)
                .subscribe(System.out::println);
        
        subscribe.dispose();
    }

    static Function<Integer, String> fizzBuzzer = (Integer i) -> {
        if (i % 15 == 0) {
            return "FizzBuzz";
        } else if (i % 3 == 0) {
            return "Fizz";
        } else if (i % 5 == 0) {
            return "Buzz";
        } else {
            return Integer.toString(i);
        }
    };
}
