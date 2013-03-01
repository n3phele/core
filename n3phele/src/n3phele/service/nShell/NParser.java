/* Generated By:JavaCC: Do not edit this line. NParser.java */
/**
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */

package n3phele.service.nShell;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import n3phele.service.model.Command;
import n3phele.service.model.CommandImplementationDefinition;
import n3phele.service.model.FileSpecification;
import n3phele.service.model.ParameterType;
import n3phele.service.model.TypedParameter;

public class NParser implements NParserConstants {
        public NParser(FileInputStream s)
        {
                this(new BlockStreamer(s));
        }

        public NParser(org.apache.commons.fileupload.FileItemStream item) throws IOException {
                this(new BlockStreamer(item.openStream()));
    }

  final public Command parse() throws ParseException {
        String name = null;
        String description = null;
        String version = "";
        boolean preferred = false;
        boolean isPublic = false;
        URI icon = null;
        List<TypedParameter> parameters = null;
        List<FileSpecification> inputFiles = null;
        List<FileSpecification> outputFiles = null;
        List<CommandImplementationDefinition> implementations = null;
    name = name();
    description = description();
    version = version();
    preferred = preferred();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      isPublic = isPublic();
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ICON:
      icon = icon();
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PARAMETERS:
      parameters = parameters();
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INPUT:
      inputFiles = inputFiles();
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OUTPUT:
      outputFiles = outputFiles();
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CLOUDNAME:
      implementations = implementations();
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    jj_consume_token(0);
                Command cd = new Command();
                cd.setShell("NShell");
                cd.setName(name);
                cd.setDescription(description);
                cd.setVersion(version);
                cd.setPreferred(preferred);
                cd.setPublic(isPublic);
                cd.setIcon(icon);
                cd.setExecutionParameters(parameters);
                cd.setInputFiles(inputFiles);
                cd.setOutputFiles(outputFiles);
                cd.setImplementations(implementations);
                if(implementations != null)
                {if (true) return cd;}
    throw new Error("Missing return statement in function");
  }

  final public String name() throws ParseException {
                  Token name;
    jj_consume_token(NAME);
    jj_consume_token(COLON);
    name = jj_consume_token(VALUE);
                {if (true) return name.toString();}
    throw new Error("Missing return statement in function");
  }

  final public String description() throws ParseException {
                         String result = null; Token t;
    jj_consume_token(DESCRIPTION);
    jj_consume_token(COLON);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DESCRIPTIONTEXT:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_1;
      }
      t = jj_consume_token(DESCRIPTIONTEXT);
                                                        result = (result == null)? t.toString() : result+" "+t.toString();
    }
          {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public String version() throws ParseException {
                     Token t;
    jj_consume_token(VERSION);
    jj_consume_token(COLON);
    t = jj_consume_token(VALUE);
          {if (true) return t.toString();}
    throw new Error("Missing return statement in function");
  }

  final public boolean preferred() throws ParseException {
                        Token t;
    jj_consume_token(PREFERRED);
    jj_consume_token(COLON);
    t = jj_consume_token(BOOLEAN_LITERAL);
          {if (true) return Boolean.valueOf(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public boolean isPublic() throws ParseException {
                       Token t;
    jj_consume_token(PUBLIC);
    jj_consume_token(COLON);
    t = jj_consume_token(BOOLEAN_LITERAL);
          {if (true) return Boolean.valueOf(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public URI icon() throws ParseException {
               Token t;
    jj_consume_token(ICON);
    jj_consume_token(COLON);
    t = jj_consume_token(URI_LITERAL);
          {if (true) return URI.create(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public List<TypedParameter> parameters() throws ParseException {
                                      List<TypedParameter> p = new ArrayList<TypedParameter>(); TypedParameter t;
    jj_consume_token(PARAMETERS);
    jj_consume_token(COLON);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPTIONAL:
      case INT:
      case FLOAT:
      case STRING:
      case SECRET:
      case BOOLEAN:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_2;
      }
      t = parameterList();
                                                     p.add(t);
    }
                {if (true) return p;}
    throw new Error("Missing return statement in function");
  }

  final public TypedParameter parameterList() throws ParseException {
                                  Token v, l, c; TypedParameter f = new TypedParameter(); String ct = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPTIONAL:
      jj_consume_token(OPTIONAL);
                        f.setOptional(true);
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
      jj_consume_token(INT);
      v = jj_consume_token(VARIABLE_NAME);
                                          f.setName(v.image); f.setDescription(v.image); f.setType(ParameterType.Long);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQUALS:
        jj_consume_token(EQUALS);
        l = jj_consume_token(INTEGER_LITERAL);
                                                                                                                                                             f.setDefaultValue(l.image);
        break;
      default:
        jj_la1[9] = jj_gen;
        ;
      }
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMENT:
          ;
          break;
        default:
          jj_la1[10] = jj_gen;
          break label_3;
        }
        c = jj_consume_token(COMMENT);
                                                                                                                                                                                                             ct = (ct==null)?c.toString():ct+" "+c; f.setDescription(ct);
      }
      break;
    case FLOAT:
      jj_consume_token(FLOAT);
      v = jj_consume_token(VARIABLE_NAME);
                                             f.setName(v.image); f.setDescription(v.image); f.setType(ParameterType.Double);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQUALS:
        jj_consume_token(EQUALS);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case FLOATING_POINT_LITERAL:
          l = jj_consume_token(FLOATING_POINT_LITERAL);
          break;
        case INTEGER_LITERAL:
          l = jj_consume_token(INTEGER_LITERAL);
          break;
        default:
          jj_la1[11] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
                                                                                                                                                                                                  f.setDefaultValue(l.image);
        break;
      default:
        jj_la1[12] = jj_gen;
        ;
      }
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMENT:
          ;
          break;
        default:
          jj_la1[13] = jj_gen;
          break label_4;
        }
        c = jj_consume_token(COMMENT);
                                                                                                                                                                                                                                                 ct = (ct==null)?c.toString():ct+" "+c; f.setDescription(ct);
      }
      break;
    case STRING:
      jj_consume_token(STRING);
      v = jj_consume_token(VARIABLE_NAME);
                                             f.setName(v.image); f.setDescription(v.image); f.setType(ParameterType.String);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQUALS:
        jj_consume_token(EQUALS);
        l = jj_consume_token(STRING_LITERAL);
                                                                                                                                                                f.setDefaultValue(l.image);
        break;
      default:
        jj_la1[14] = jj_gen;
        ;
      }
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMENT:
          ;
          break;
        default:
          jj_la1[15] = jj_gen;
          break label_5;
        }
        c = jj_consume_token(COMMENT);
                                                                                                                                                                                                                ct = (ct==null)?c.toString():ct+" "+c; f.setDescription(ct);
      }
      break;
    case SECRET:
      jj_consume_token(SECRET);
      v = jj_consume_token(VARIABLE_NAME);
                                             f.setName(v.image); f.setDescription(v.image); f.setType(ParameterType.Secret);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQUALS:
        jj_consume_token(EQUALS);
        l = jj_consume_token(STRING_LITERAL);
                                                                                                                                                                f.setDefaultValue(l.image);
        break;
      default:
        jj_la1[16] = jj_gen;
        ;
      }
      label_6:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMENT:
          ;
          break;
        default:
          jj_la1[17] = jj_gen;
          break label_6;
        }
        c = jj_consume_token(COMMENT);
                                                                                                                                                                                                               ct = (ct==null)?c.toString():ct+" "+c; f.setDescription(ct);
      }
      break;
    case BOOLEAN:
      jj_consume_token(BOOLEAN);
      v = jj_consume_token(VARIABLE_NAME);
                                              f.setName(v.image); f.setDescription(v.image); f.setType(ParameterType.Boolean);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQUALS:
        jj_consume_token(EQUALS);
        l = jj_consume_token(BOOLEAN_LITERAL);
                                                                                                                                                                   f.setDefaultValue(l.image);
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
      label_7:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMENT:
          ;
          break;
        default:
          jj_la1[19] = jj_gen;
          break label_7;
        }
        c = jj_consume_token(COMMENT);
                                                                                                                                                                                                                   ct = (ct==null)?c.toString():ct+" "+c; f.setDescription(ct);
      }
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return f;}
    throw new Error("Missing return statement in function");
  }

  final public List<FileSpecification> inputFiles() throws ParseException {
                                         List<FileSpecification> p = new ArrayList<FileSpecification>(); FileSpecification t;
    jj_consume_token(INPUT);
    jj_consume_token(COLON);
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPTIONAL:
      case FILESPEC:
        ;
        break;
      default:
        jj_la1[21] = jj_gen;
        break label_8;
      }
      t = inputFile();
                                            p.add(t);
    }
                {if (true) return p;}
    throw new Error("Missing return statement in function");
  }

  final public FileSpecification inputFile() throws ParseException {
                                  Token s, c; FileSpecification f = new FileSpecification(); String ct = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPTIONAL:
      jj_consume_token(OPTIONAL);
                       f.setOptional(true);
      break;
    default:
      jj_la1[22] = jj_gen;
      ;
    }
    s = jj_consume_token(FILESPEC);
                                                                 f.setName(s.image);
    label_9:
    while (true) {
      c = jj_consume_token(COMMENT);
                                                                                                        ct = (ct==null)?c.toString():ct+" "+c; f.setDescription(ct);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMENT:
        ;
        break;
      default:
        jj_la1[23] = jj_gen;
        break label_9;
      }
    }
           {if (true) return f;}
    throw new Error("Missing return statement in function");
  }

  final public List<FileSpecification> outputFiles() throws ParseException {
                                          List<FileSpecification> p = new ArrayList<FileSpecification>(); FileSpecification t;
    jj_consume_token(OUTPUT);
    jj_consume_token(COLON);
    label_10:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FILESPEC:
        ;
        break;
      default:
        jj_la1[24] = jj_gen;
        break label_10;
      }
      t = outputFile();
                                              p.add(t);
    }
                {if (true) return p;}
    throw new Error("Missing return statement in function");
  }

  final public FileSpecification outputFile() throws ParseException {
                                   Token s, c; FileSpecification f = new FileSpecification(); String ct = null;
    s = jj_consume_token(FILESPEC);
                          f.setName(s.image);
    label_11:
    while (true) {
      c = jj_consume_token(COMMENT);
                                                                 ct = (ct==null)?c.toString():ct+" "+c; f.setDescription(ct);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMENT:
        ;
        break;
      default:
        jj_la1[25] = jj_gen;
        break label_11;
      }
    }
           {if (true) return f;}
    throw new Error("Missing return statement in function");
  }

  final public List<CommandImplementationDefinition> implementations() throws ParseException {
  List<CommandImplementationDefinition> p = new ArrayList<CommandImplementationDefinition >();
  CommandImplementationDefinition cid;
    label_12:
    while (true) {
      cid = implementation();
                                   p.add(cid);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CLOUDNAME:
        ;
        break;
      default:
        jj_la1[26] = jj_gen;
        break label_12;
      }
    }
                {if (true) return p;}
    throw new Error("Missing return statement in function");
  }

  final public CommandImplementationDefinition implementation() throws ParseException {
                                                     Token c; Token t = null; Token b = null;
    c = jj_consume_token(CLOUDNAME);
    jj_consume_token(COLON);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ANNOTATION:
      t = jj_consume_token(ANNOTATION);
      break;
    default:
      jj_la1[27] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IMPLEMENTATION:
      b = jj_consume_token(IMPLEMENTATION);
      break;
    default:
      jj_la1[28] = jj_gen;
      ;
    }
    jj_consume_token(END);
                String name = c.image;
                String annotation = (t == null)? "" : t.image;
                String body = (b == null)? null : b.image;
                CommandImplementationDefinition cid = new CommandImplementationDefinition(name, annotation, body, c.beginLine+1);
                cid.setCompiled(new n3phele.service.nShell.Shell(cid.getBody(), cid.getLineNo()).compile());
                {if (true) return cid;}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public NParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[29];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x800,0x1000,0x8000,0x2000,0x4000,0x0,0x0,0xfc00000,0x400000,0x10000000,0x100000,0x20000000,0x10000000,0x100000,0x10000000,0x100000,0x10000000,0x100000,0x10000000,0x100000,0xf800000,0x400000,0x400000,0x100000,0x0,0x100000,0x0,0x0,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x200000,0x10000,0x0,0x0,0x0,0x0,0x2,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x200,0x0,0x0,0x200,0x0,0x200000,0x10000000,0x8000000,};
   }

  /** Constructor with InputStream. */
  public NParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public NParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new NParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public NParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new NParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public NParser(NParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(NParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[61];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 29; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 61; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
/*
* BlockStream must be applied to input streams sent to the parser for correct operation. The grammar processing
* relies on its pre-treatment. In particular:
* 	- \r characters are removed from the raw input stream
*	- The \n character prior to non-indented text is changed to a \r\n sequence  
*/
class BlockStreamer extends InputStream {
        InputStream stream;
        Integer lookahead = null;
        boolean nextIsNL = false;
        public BlockStreamer(InputStream stream) {
                this.stream = stream;
        }

        @Override
        public int read() throws IOException {
                if(nextIsNL) {
                        nextIsNL = false;
                        return '\u005cn';
                }
                int c;
                if(lookahead != null) {
                        c = lookahead;
                        lookahead = null;
                } else {
                        c = stream.read();
                }

                if (c == -1) {
                    return -1;
                }
                if(c == '\u005cr')
                        return read();
                if(c == '\u005cn') {
                        while((lookahead = stream.read()) == '\u005cr') ;
                        if(lookahead == ' ' || lookahead == '\u005ct' || lookahead == '\u005cn' ) {
                                return '\u005cn';
                        } else {
                                nextIsNL = true;
                                return '\u005cr';
                        }
                }

                return c;
        }

        @Override
        public void close() throws IOException {
                stream.close();
        }

}
