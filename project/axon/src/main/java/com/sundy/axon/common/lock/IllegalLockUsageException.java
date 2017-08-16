package com.sundy.axon.common.lock;

import com.sundy.axon.common.AxonNonTransientException;

/**
 * Exception indicating that an illegal use of a lock was detect (e.g. releasing a lock that wasn't previously
 * obtained)
 * <p/>
 * Typically, operations failing with this exception cannot be retried without the application taking appropriate
 * measures first.
 *
 * @author Allard Buijze
 * @since 2.0
 */
public class IllegalLockUsageException extends AxonNonTransientException {
	private static final long serialVersionUID = 4453369833513201587L;

    /**
     * Initialize the exception with given <code>message</code> and <code>cause</code>
     *
     * @param message The message describing the exception
     * @param cause   The underlying cause of the exception
     */
    public IllegalLockUsageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Initialize the exception with given <code>message</code>.
     *
     * @param message The message describing the exception
     */
    public IllegalLockUsageException(String message) {
        super(message);
    }
}
