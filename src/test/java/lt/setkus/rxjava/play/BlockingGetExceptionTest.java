package lt.setkus.rxjava.play;

import org.junit.Before;
import org.junit.Test;

public class BlockingGetExceptionTest {

    private final RuntimeException error = new RuntimeException("Ooops...");
    private BlockingGetException blockingGetException;

    @Before
    public void setUp() {
        blockingGetException = new BlockingGetException(error);
    }

    @Test
    public void whenObservableEmitterDoesNotReceiveErrorThenAssertionShouldFail() {
        blockingGetException.buildNoOnErrorPushObservable().test()
                .assertError(e -> e.equals(error))
                .assertNoValues();
    }

    @Test
    public void whenBuildingErrorObservableThenShouldEmitError() {
        blockingGetException.buildWithErrorPushObservable().test()
                .assertError(e -> e.equals(error))
                .assertNoValues();
    }
}