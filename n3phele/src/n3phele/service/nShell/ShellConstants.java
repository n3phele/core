/* Generated By:JJTree&JavaCC: Do not edit this line. ShellConstants.java */
package n3phele.service.nShell;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ShellConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int COMMENT = 3;
  /** RegularExpression Id. */
  int SPACE = 4;
  /** RegularExpression Id. */
  int EXPRESSIONWRAPPER = 5;
  /** RegularExpression Id. */
  int ON = 6;
  /** RegularExpression Id. */
  int CREATEVM = 7;
  /** RegularExpression Id. */
  int FORLOOP = 8;
  /** RegularExpression Id. */
  int IF = 9;
  /** RegularExpression Id. */
  int ELSE = 10;
  /** RegularExpression Id. */
  int ASSIMILATE = 11;
  /** RegularExpression Id. */
  int DESTROY = 12;
  /** RegularExpression Id. */
  int LOG = 13;
  /** RegularExpression Id. */
  int VARIABLEASSIGN = 14;
  /** RegularExpression Id. */
  int VARSTART = 15;
  /** RegularExpression Id. */
  int VARREST = 16;
  /** RegularExpression Id. */
  int WHITESPACE = 17;
  /** RegularExpression Id. */
  int NO_ARG_OPTION = 18;
  /** RegularExpression Id. */
  int OPTION = 19;
  /** RegularExpression Id. */
  int OPTIONSTART = 20;
  /** RegularExpression Id. */
  int OPTIONREST = 21;
  /** RegularExpression Id. */
  int NON_SPACE_ARG = 22;
  /** RegularExpression Id. */
  int ARGSTART = 23;
  /** RegularExpression Id. */
  int ARGREST = 24;
  /** RegularExpression Id. */
  int LITERAL_STRING = 25;
  /** RegularExpression Id. */
  int FILELIST = 26;
  /** RegularExpression Id. */
  int FILESPEC = 27;
  /** RegularExpression Id. */
  int FILESTART = 28;
  /** RegularExpression Id. */
  int FILEREST = 29;
  /** RegularExpression Id. */
  int COMMA = 30;
  /** RegularExpression Id. */
  int FILELISTEND = 31;
  /** RegularExpression Id. */
  int COLON = 32;
  /** RegularExpression Id. */
  int LITERALBLOCK = 33;
  /** RegularExpression Id. */
  int VARIABLE = 34;
  /** RegularExpression Id. */
  int EXPRESSION = 35;
  /** RegularExpression Id. */
  int STRING_LITERAL = 36;
  /** RegularExpression Id. */
  int PASSTHRU = 37;
  /** RegularExpression Id. */
  int PASSTHRUSET = 38;
  /** RegularExpression Id. */
  int PASSTHRUMORE = 39;
  /** RegularExpression Id. */
  int WRAPPEDEXPRESSION = 40;

  /** Lexical state. */
  int fileListProcessing = 0;
  /** Lexical state. */
  int DEFAULT = 1;
  /** Lexical state. */
  int argProcessing = 2;
  /** Lexical state. */
  int passThruProcessing = 3;
  /** Lexical state. */
  int wrappedExpressionProcessing = 4;
  /** Lexical state. */
  int expressionProcessing = 5;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\"\\r\"",
    "\"\\n\"",
    "<COMMENT>",
    "<SPACE>",
    "\"$${\"",
    "\"ON\"",
    "\"CREATEVM\"",
    "\"FOR\"",
    "\"IF\"",
    "\"ELSE\"",
    "\"ASSIMILATE\"",
    "\"DESTROY\"",
    "\"LOG\"",
    "<VARIABLEASSIGN>",
    "<VARSTART>",
    "<VARREST>",
    "<WHITESPACE>",
    "<NO_ARG_OPTION>",
    "<OPTION>",
    "<OPTIONSTART>",
    "<OPTIONREST>",
    "<NON_SPACE_ARG>",
    "<ARGSTART>",
    "<ARGREST>",
    "<LITERAL_STRING>",
    "\"[\"",
    "<FILESPEC>",
    "<FILESTART>",
    "<FILEREST>",
    "\",\"",
    "\"]\"",
    "\":\"",
    "<LITERALBLOCK>",
    "<VARIABLE>",
    "<EXPRESSION>",
    "<STRING_LITERAL>",
    "<PASSTHRU>",
    "<PASSTHRUSET>",
    "<PASSTHRUMORE>",
    "<WRAPPEDEXPRESSION>",
  };

}
