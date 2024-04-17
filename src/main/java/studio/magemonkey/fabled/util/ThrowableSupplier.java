package studio.magemonkey.fabled.util;

public interface ThrowableSupplier<T> {
    T get() throws Exception;

    @SafeVarargs
    static <A> A tryWithRecovers(ThrowableSupplier<A>... trials) {
        for (int i = 0; i < trials.length; i++) {
            ThrowableSupplier<A> trial = trials[i];
            try {
                return trial.get();
            } catch (NoSuchMethodError | ReflectiveOperationException e) {
                // ignore
            } catch (Exception e) {
                throw new RuntimeException("Error while executing volatile code: " + i, e);
            }
        }
        throw new IllegalStateException("No volatile code has matched");
    }
}
