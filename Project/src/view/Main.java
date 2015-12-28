/**
 * 
 */
package view;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Danielson Flávio Xavier da Silva
 * The start of the program
 */
public class Main {
	
	// Variable for intercionalization
    private ResourceBundle messages;
    /**
     * Constructor for the Main Class
     */
    public Main(ResourceBundle message)
    {
        // Initialize the variable responsible for internationalization
        this.messages = message;
        General general = new General(messages);
    }

    /**
     * Executing the start of the program
     * 
     * @param  args   arguments for internationalization
     * @opt br BR Make the program in Brazilian Portuguese
     * @opt en US Make the program in English
     * @opt fr FR Make the program in French
     */
	public static void main(String[] args) {
        String language;
        String country;

        if (args.length != 2) {
            language = new String("br");
            country = new String("BR");
        } else {
            language = new String(args[0]);
            country = new String(args[1]);
        }

        Locale currentLocale;
        currentLocale = new Locale(language, country);
        ResourceBundle international = ResourceBundle.getBundle("resources.MessagesBundle", currentLocale);
        
        Main main = new Main(international);
	}

}
