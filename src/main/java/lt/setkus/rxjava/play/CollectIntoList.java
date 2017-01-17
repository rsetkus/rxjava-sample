/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.setkus.rxjava.play;

import io.reactivex.Observable;
import java.util.List;

/**
 *
 * If you want to collect some data into a list somehwere...
 */
public class CollectIntoList {

    public static void main(String[] args) {
        
        // If you want a reference to a list
        List<Double> doubleList = Observable.range(0, 50)
                .map((Integer i) -> (double) i / 2)
                .toList()
                .blockingGet();
        
        System.out.println(doubleList);
        
        // If you want to apply a function (Consumer<list>) to determine what to do with the list, implement a list consumer
        Observable.range(0, 50)
                .map((Integer i) -> (double) i / 2)
                .toList()
                .subscribe(list -> System.out.println(list));
                
    }
}
