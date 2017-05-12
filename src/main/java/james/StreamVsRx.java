package james;

import io.reactivex.Observable;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StreamVsRx {

    public static void main(String[] args) {

        List<Integer> nums = Arrays.asList(1,2,3,4,5,6,7,8);

        nums.stream()
                .map(i -> i.toString() + "j8")
                .forEach(System.out::println);

        Observable.fromIterable(nums)
                .map(i -> i.toString() + "rx")
                .subscribe(System.out::println);

        List<String> j8Nums = nums.stream()
                .map(i -> i.toString() + "j8")
                .collect(toList());

        List<String> rxNums = Observable.fromIterable(nums)
                .map(i -> i.toString() + "rx")
                .toList()
                .blockingGet();

    }
}
