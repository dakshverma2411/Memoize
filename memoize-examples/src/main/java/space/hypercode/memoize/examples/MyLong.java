package space.hypercode.memoize.examples;

import lombok.Builder;
import lombok.Value;
import space.hypercode.core.models.Memoizable;


/**
 * Immutable wrapper around a {@link Long} value that implements {@link Memoizable}.
 *
 * <p>Uses Lombok {@link Value} and {@link Builder} for boilerplate-free immutability.
 * The memoization key is the decimal string representation of the wrapped value.
 */
@Value
@Builder
public class MyLong implements Memoizable {

    /** The wrapped long value. */
    Long value;

    /**
     * Returns the string representation of the wrapped value as the memoization key.
     *
     * @return the decimal string form of {@link #value}
     */
    @Override
    public String memoizationKey() {
        return value.toString();
    }
}
