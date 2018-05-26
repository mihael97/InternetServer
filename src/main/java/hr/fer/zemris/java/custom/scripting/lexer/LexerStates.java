package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Javni enum koji sadrzi moguca stanja rada lexera(TAG i NONTAG)
 * 
 * @author Mihael
 *
 */
public enum LexerStates {

	/**
	 * Nacin rada kada lexer proizvodi tokene unutar tagova
	 */
	TAG,

	/**
	 * Nacin rada lexera kada proizvodi tokene van tagova
	 */
	NONTAG;

}
