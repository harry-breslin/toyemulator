package myPackage;

/**
 * Superclass for all TOY-related exceptions. All {@code TOYException}s are
 * checked.
 */
class TOYException extends Exception {
	static String defaultMessage;

	/**
	 * Empty {@code TOYException} constructor.
	 */
	public TOYException() {
	}

	/**
	 * Constructs a new {@code TOYException} with a given message.
	 * 
	 * @param message message to use
	 */
	public TOYException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code TOYException} with a given cause.
	 * 
	 * @param cause cause to use
	 */
	public TOYException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code TOYException} with a given message and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public TOYException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Superclass for all {@link TOYException}s related to something being undefined
 * or uninitialised.
 */
class UninitialisedException extends TOYException {
	/**
	 * Empty {@code UninitialisedException} constructor.
	 */
	public UninitialisedException() {
	}

	/**
	 * Constructs a new {@code UninitialisedException} with a given message.
	 * 
	 * @param message message to use
	 */
	public UninitialisedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code UninitialisedException} with a given cause.
	 * 
	 * @param cause cause to use
	 */
	public UninitialisedException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code UninitialisedException} with a given message and
	 * cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public UninitialisedException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Thrown when the result of an arithmetic operation is outside the range of
 * valid TOY numbers.
 */
class OverflowException extends TOYException {
	static final String defaultMessage = "The result of an operation was not between " + Short.MIN_VALUE + " and "
			+ Short.MAX_VALUE;

	/**
	 * Default {@code OverflowException} constructor - calls
	 * {@link #OverflowException(String)} with a {@code message} of: <blockquote>
	 * {@value #defaultMessage} </blockquote>
	 */
	public OverflowException() {
		this(defaultMessage);
	}

	/**
	 * Constructs a new {@code OverflowException} with a given message.
	 * 
	 * @param message message to use
	 */
	public OverflowException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code OverflowException} with a given cause.
	 * 
	 * @param cause cause to use
	 */
	public OverflowException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code OverflowException} with a given message and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public OverflowException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Superclass for all {@link TOYException}s related to something being out of
 * bounds.
 */
class OutOfBoundsException extends TOYException {
	/**
	 * Empty {@code OutOfBoundsException} constructor.
	 */
	public OutOfBoundsException() {
	}

	/**
	 * Constructs a new {@code OutOfBoundsException} with a given message.
	 * 
	 * @param message message to use
	 */
	public OutOfBoundsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code OutOfBoundsException} with a given cause.
	 * 
	 * @param cause cause to use
	 */
	public OutOfBoundsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code OutOfBoundsException} with a given message and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public OutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Thrown when a register required by an {@link Instruction} is uninitialised.
 */
class RegisterUninitialisedException extends UninitialisedException {
	static final String defaultMessage = "A register referenced is undefined";

	/**
	 * Default {@code RegisterUninitialisedException} constructor - calls
	 * {@link #RegisterUninitialisedException(String)} with a {@code message} of:
	 * <blockquote> {@value #defaultMessage} </blockquote>
	 */
	public RegisterUninitialisedException() {
		this(defaultMessage);
	}

	/**
	 * Constructs a new {@code RegisterUninitialisedException} with a given message.
	 * 
	 * @param message message to use
	 */
	public RegisterUninitialisedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code RegisterUninitialisedException} with a given cause.
	 * 
	 * @param cause cause to use
	 */
	public RegisterUninitialisedException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code RegisterUninitialisedException} with a given message
	 * and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public RegisterUninitialisedException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Thrown when a memory address an {@link Instruction} accessed is
 * uninitialised.
 */
class MemoryUninitialisedException extends UninitialisedException {
	static final String defaultMessage = "A memory address referenced is undefined";

	/**
	 * Default {@code MemoryUninitialisedException} constructor - calls
	 * {@link #MemoryUninitialisedException(String)} with a {@code message} of:
	 * <blockquote> {@value #defaultMessage} </blockquote>
	 */
	public MemoryUninitialisedException() {
		this(defaultMessage);
	}

	/**
	 * Constructs a new {@code MemoryUninitialisedException} with a given message.
	 * 
	 * @param message message to use
	 */
	public MemoryUninitialisedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code MemoryUninitialisedException} with a given cause.
	 * 
	 * @param cause cause to use
	 */
	public MemoryUninitialisedException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code MemoryUninitialisedException} with a given message
	 * and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public MemoryUninitialisedException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Thrown when the {@link Instruction} at the current
 * {@linkplain Program#programCounter program counter} is uninitialised.
 */
class CommandUninitialisedException extends UninitialisedException {
	static final String defaultMessage = "Line is undefined";

	/**
	 * Default {@code CommandUninitialisedException} constructor - calls
	 * {@link #CommandUninitialisedException(String)} with a {@code message} of:
	 * <blockquote> {@value #defaultMessage} </blockquote>
	 */
	public CommandUninitialisedException() {
		this(defaultMessage);
	}

	/**
	 * Constructs a new {@code CommandUninitialisedException} with a given message.
	 * 
	 * @param message message to use
	 */
	public CommandUninitialisedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code CommandUninitialisedException} with a given cause.
	 * 
	 * @param cause cause to use
	 */
	public CommandUninitialisedException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code CommandUninitialisedException} with a given message
	 * and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public CommandUninitialisedException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Thrown when the magnitude used for a shift operation is outside the allowed
 * range.
 */
class ShiftMagnitudeOutOfBoundsException extends OutOfBoundsException {
	static final String defaultMessage = "An invalid shift magnitude was used; shift magnitudes must be between 0000 and 000F";

	/**
	 * Default {@code ShiftMagnitudeOutOfBoundsException} constructor - calls
	 * {@link #ShiftMagnitudeOutOfBoundsException(String)} with a {@code message}
	 * of: <blockquote> {@value #defaultMessage} </blockquote>
	 */
	public ShiftMagnitudeOutOfBoundsException() {
		this(defaultMessage);
	}

	/**
	 * Constructs a new {@code ShiftMagnitudeOutOfBoundsException} with a given
	 * message.
	 * 
	 * @param message message to use
	 */
	public ShiftMagnitudeOutOfBoundsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code ShiftMagnitudeOutOfBoundsException} with a given
	 * cause.
	 * 
	 * @param cause cause to use
	 */
	public ShiftMagnitudeOutOfBoundsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code ShiftMagnitudeOutOfBoundsException} with a given
	 * message and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public ShiftMagnitudeOutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Thrown when an {@link Instruction} attempted to set the program counter to a
 * value outside the allowed range.
 */
class ProgramCounterOutOfBoundsException extends OutOfBoundsException {
	static final String defaultMessage = "An instruction attempted to set an invalid program counter value; must be between 00 and FF";

	/**
	 * Default {@code ProgramCounterOutOfBoundsException} constructor - calls
	 * {@link #ProgramCounterOutOfBoundsException(String)} with a {@code message}
	 * of: <blockquote> {@value #defaultMessage} </blockquote>
	 */
	public ProgramCounterOutOfBoundsException() {
		this(defaultMessage);
	}

	/**
	 * Constructs a new {@code ProgramCounterOutOfBoundsException} with a given
	 * message.
	 * 
	 * @param message message to use
	 */
	public ProgramCounterOutOfBoundsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code ProgramCounterOutOfBoundsException} with a given
	 * cause.
	 * 
	 * @param cause cause to use
	 */
	public ProgramCounterOutOfBoundsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code ProgramCounterOutOfBoundsException} with a given
	 * message and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public ProgramCounterOutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Thrown when an {@link Instruction} attempted to change R[0].
 */
class RegisterIndexOutOfBoundsException extends OutOfBoundsException {
	static final String defaultMessage = "An instruction attempted to change R[0]";

	/**
	 * Default {@code RegisterIndexOutOfBoundsException} constructor - calls
	 * {@link #RegisterIndexOutOfBoundsException(String)} with a {@code message} of:
	 * <blockquote> {@value #defaultMessage} </blockquote>
	 */
	public RegisterIndexOutOfBoundsException() {
		this(defaultMessage);
	}

	/**
	 * Constructs a new {@code RegisterIndexOutOfBoundsException} with a given
	 * message.
	 * 
	 * @param message message to use
	 */
	public RegisterIndexOutOfBoundsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code RegisterIndexOutOfBoundsException} with a given
	 * cause.
	 * 
	 * @param cause cause to use
	 */
	public RegisterIndexOutOfBoundsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code RegisterIndexOutOfBoundsException} with a given
	 * message and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public RegisterIndexOutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}
}

/**
 * Thrown when an {@link Instruction} attempted to access an address outside the
 * allowed range of memory addresses.
 */
class MemoryAddressOutOfBoundsException extends OutOfBoundsException {
	static final String defaultMessage = "An instruction attempted to store to or load from and invalid memory address; must be between 00 and FF";

	/**
	 * Default {@code MemoryAddressOutOfBoundsException} constructor - calls
	 * {@link #MemoryAddressOutOfBoundsException(String)} with a {@code message} of:
	 * <blockquote> {@value #defaultMessage} </blockquote>
	 */
	public MemoryAddressOutOfBoundsException() {
		this(defaultMessage);
	}

	/**
	 * Constructs a new {@code MemoryAddressOutOfBoundsException} with a given
	 * message.
	 * 
	 * @param message message to use
	 */
	public MemoryAddressOutOfBoundsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code MemoryAddressOutOfBoundsException} with a given
	 * cause.
	 * 
	 * @param cause cause to use
	 */
	public MemoryAddressOutOfBoundsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code MemoryAddressOutOfBoundsException} with a given
	 * message and cause.
	 * 
	 * @param message message to use
	 * @param cause   cause to use
	 */
	public MemoryAddressOutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}
}