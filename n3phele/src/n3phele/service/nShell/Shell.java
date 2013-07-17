/* Generated By:JJTree&JavaCC: Do not edit this line. Shell.java */
package n3phele.service.nShell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import n3phele.service.model.ShellFragment;
/** * (C) Copyright 2010-2013. Nigel Cook. All rights reserved. * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *  * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file * except in compliance with the License.  *  *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  *  specific language governing permissions and limitations under the License. */

public class Shell/*@bgen(jjtree)*/implements ShellTreeConstants, ShellConstants {/*@bgen(jjtree)*/
  protected JJTShellState jjtree = new JJTShellState();protected Expression expressionHandler = null;
        public Shell(String s, int lineNo)
        {
                if(s.charAt(0) != '\u005cn')
                {
                        lineNo = lineNo -1;
                        s = "\u005cn"+s;
           }
                jj_input_stream = new SimpleCharStream(new java.io.StringReader(s), lineNo, 1);
        jj_input_stream.setTabSize(4);
        token_source = new ShellTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 5; i++) jj_la1[i] = -1;
        }

        public Expression getExpressionHandler(String s, int lineNo, int columnNo)
        {
                if(expressionHandler == null)
                {
                        return (expressionHandler = new Expression(s, lineNo, columnNo));
                } else
                {
                        expressionHandler.reInit(s, lineNo, columnNo);
                        return expressionHandler;
                }

        }
    public SimpleNode getExpressionNode(String s, int lineNo, int columnNo) throws ParseException
        {
                Expression e = getExpressionHandler(s, lineNo, columnNo);
                SimpleNode node = e.buildExpressionTree();
                return node;
    }
         public List<ShellFragment> compile() throws ParseException
     {
        List<ShellFragment> result = new ArrayList<ShellFragment>();
        SelfCompilingNode s = script();
        s.compile(result);
        return result;
     }

  final public SelfCompilingNode script() throws ParseException {
                              /*@bgen(jjtree) script */
  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTSCRIPT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ON:
        case CREATEVM:
        case FORLOOP:
        case EXPORT:
        case ASSIMILATEVM:
        case DESTROY:
        case LOG:
        case VARIABLEASSIGN:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        command();
      }
      jj_consume_token(0);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
        {if (true) return (SelfCompilingNode) jjtn000;}
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

  final public void command() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ON:
    case CREATEVM:
    case ASSIMILATEVM:
    case DESTROY:
    case LOG:
      simpleCommand();
      break;
    case FORLOOP:
      blockCommand();
      break;
    case VARIABLEASSIGN:
      variableAssign();
      break;
    case EXPORT:
      export();
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void simpleCommand() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATEVM:
      createvm();
      break;
    case ON:
      on();
      break;
    case LOG:
      log();
      break;
    case DESTROY:
      destroy();
      break;
    case ASSIMILATEVM:
      assimilatevm();
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void blockCommand() throws ParseException {
    forCommand();
  }

  final public void forCommand() throws ParseException {
                     /*@bgen(jjtree) forCommand */
                      SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTFORCOMMAND);
                      boolean jjtc000 = true;
                      jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(FORLOOP);
      variable();
      jj_consume_token(COLON);
      expression();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COLON:
        jj_consume_token(COLON);
        expression();
        break;
      default:
        jj_la1[3] = jj_gen;
        ;
      }
      block(t.beginLine, t.beginColumn);
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  final public void variable() throws ParseException {
                   /*@bgen(jjtree) variable */
                    SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTVARIABLE);
                    boolean jjtc000 = true;
                    jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(VARIABLE);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
                jjtn000.jjtSetValue(t.image);
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  final public void block(int blockLine, int indent) throws ParseException {
                                         /*@bgen(jjtree) block */
  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTBLOCK);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      label_2:
      while (true) {
        if (getToken(1).kind != EOF &&
                                indent < getToken(1).beginColumn) {
          ;
        } else {
          break label_2;
        }
        command();
      }
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void createvm() throws ParseException {
                  /*@bgen(jjtree) createvm */
  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTCREATEVM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(CREATEVM);
      label_3:
      while (true) {
        option();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NO_ARG_OPTION:
        case OPTION:
          ;
          break;
        default:
          jj_la1[4] = jj_gen;
          break label_3;
        }
      }
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void option() throws ParseException {
                 /*@bgen(jjtree) option */
                  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTOPTION);
                  boolean jjtc000 = true;
                  jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPTION:
        t = jj_consume_token(OPTION);
        arg();
        break;
      case NO_ARG_OPTION:
        t = jj_consume_token(NO_ARG_OPTION);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
                jjtn000.jjtSetValue(t.image);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void arg() throws ParseException {
                     Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NON_SPACE_ARG:
    case LITERAL_STRING:
    case LITERALBLOCK:
      literalArg();
      break;
    case VARIABLE:
    case EXPRESSION:
    case WRAPPEDEXPRESSION:
      expression();
      break;
    case FILELIST:
      fileList();
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void literalArg() throws ParseException {
                     /*@bgen(jjtree) literalArg */
                      SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTLITERALARG);
                      boolean jjtc000 = true;
                      jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NON_SPACE_ARG:
        t = jj_consume_token(NON_SPACE_ARG);
        break;
      case LITERAL_STRING:
        t = jj_consume_token(LITERAL_STRING);
        break;
      case LITERALBLOCK:
        t = jj_consume_token(LITERALBLOCK);
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.jjtSetValue(t.image);
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void on() throws ParseException {
             /*@bgen(jjtree) on */
  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTON);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(ON);
      expression();
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NO_ARG_OPTION:
        case OPTION:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_4;
        }
        option();
      }
      pieces();
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void log() throws ParseException {
              /*@bgen(jjtree) log */
  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTLOG);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(LOG);
      pieces();
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void destroy() throws ParseException {
                  /*@bgen(jjtree) destroy */
  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTDESTROY);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(DESTROY);
      expression();
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void assimilatevm() throws ParseException {
                       /*@bgen(jjtree) assimilatevm */
  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTASSIMILATEVM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(ASSIMILATEVM);
      label_5:
      while (true) {
        option();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NO_ARG_OPTION:
        case OPTION:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_5;
        }
      }
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void export() throws ParseException {
                 /*@bgen(jjtree) export */
                  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTEXPORT);
                  boolean jjtc000 = true;
                  jjtree.openNodeScope(jjtn000);Token t;
    try {
      jj_consume_token(EXPORT);
      t = jj_consume_token(VARIABLE);
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          jjtn000.jjtSetValue(t.image);
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void variableAssign() throws ParseException {
                         /*@bgen(jjtree) variableAssign */
                          SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTVARIABLEASSIGN);
                          boolean jjtc000 = true;
                          jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(VARIABLEASSIGN);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ON:
      case CREATEVM:
      case ASSIMILATEVM:
      case DESTROY:
      case LOG:
        simpleCommand();
        break;
      case VARIABLE:
      case EXPRESSION:
      case WRAPPEDEXPRESSION:
        expression();
        break;
      default:
        jj_la1[10] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          jjtn000.jjtSetValue(t.image);
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void expression() throws ParseException {
                     /*@bgen(jjtree) expression */
                      SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTEXPRESSION);
                      boolean jjtc000 = true;
                      jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case VARIABLE:
        t = jj_consume_token(VARIABLE);
                                                  jjtree.pushNode(getExpressionNode(t.image, t.beginLine, t.beginColumn));
        break;
      case WRAPPEDEXPRESSION:
        t = jj_consume_token(WRAPPEDEXPRESSION);
                                          jjtree.pushNode(getExpressionNode(t.image, t.beginLine, t.beginColumn));
        break;
      case EXPRESSION:
        t = jj_consume_token(EXPRESSION);
                                                  jjtree.pushNode(getExpressionNode(t.image, t.beginLine, t.beginColumn));
        break;
      default:
        jj_la1[11] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
                if(t.specialToken != null)
                        jjtn000.jjtSetValue(t.specialToken.image);
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public Token passThruFragment() throws ParseException {
                                   Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PASSTHRU:
      t = jj_consume_token(PASSTHRU);
      break;
    case PASSTHRUMORE:
      t = jj_consume_token(PASSTHRUMORE);
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return t;}
    throw new Error("Missing return statement in function");
  }

  final public void passThru(boolean lastWasExp) throws ParseException {
                                     /*@bgen(jjtree) passThru */
                                      SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTPASSTHRU);
                                      boolean jjtc000 = true;
                                      jjtree.openNodeScope(jjtn000);Token t; String result=null; Token first=null; String prefix = "";
    try {
      label_6:
      while (true) {
        t = passThruFragment();
                  if(first == null)
                  {
                        first = t;
                        if(lastWasExp)  {
                          if(first.specialToken != null) {
                                prefix = first.specialToken.image;
                          }
                    }
                  }
                  result = result == null ? prefix+t.image : result+(t.specialToken != null?t.specialToken.image:"")+t.image;
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case PASSTHRU:
        case PASSTHRUMORE:
          ;
          break;
        default:
          jj_la1[13] = jj_gen;
          break label_6;
        }
      }
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          jjtn000.jjtSetValue(result);
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void pieces() throws ParseException {
                 /*@bgen(jjtree) pieces */
                  SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTPIECES);
                  boolean jjtc000 = true;
                  jjtree.openNodeScope(jjtn000);boolean lastWasExp = false;
    try {
      label_7:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case VARIABLE:
        case EXPRESSION:
        case WRAPPEDEXPRESSION:
          expression();
             lastWasExp = true;
          break;
        case PASSTHRU:
        case PASSTHRUMORE:
          passThru(lastWasExp);
            lastWasExp = false;
          break;
        default:
          jj_la1[14] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case VARIABLE:
        case EXPRESSION:
        case PASSTHRU:
        case PASSTHRUMORE:
        case WRAPPEDEXPRESSION:
          ;
          break;
        default:
          jj_la1[15] = jj_gen;
          break label_7;
        }
      }
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void fileList() throws ParseException {
                   /*@bgen(jjtree) fileList */
                    SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTFILELIST);
                    boolean jjtc000 = true;
                    jjtree.openNodeScope(jjtn000);Token f; Token m;
    try {
      jj_consume_token(FILELIST);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FILESPEC:
        fileElement();
        label_8:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case COMMA:
            ;
            break;
          default:
            jj_la1[16] = jj_gen;
            break label_8;
          }
          jj_consume_token(COMMA);
          fileElement();
        }
        break;
      default:
        jj_la1[17] = jj_gen;
        ;
      }
      jj_consume_token(FILELISTEND);
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void fileElement() throws ParseException {
                      /*@bgen(jjtree) fileElement */
                       SelfCompilingNode jjtn000 = (SelfCompilingNode)n3phele.service.nShell.ShellNode.jjtCreate(JJTFILEELEMENT);
                       boolean jjtc000 = true;
                       jjtree.openNodeScope(jjtn000);Token f; Token m;
    try {
      f = jj_consume_token(FILESPEC);
                           m = f;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COLON:
        jj_consume_token(COLON);
        m = jj_consume_token(FILESPEC);
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
                                                                    jjtree.closeNodeScope(jjtn000, true);
                                                                    jjtc000 = false;
                                                                    jjtn000.jjtSetValue(f.image+":"+m.image);
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  /** Generated Token Manager. */
  public ShellTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[19];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0xf9c0,0xf9c0,0x70c0,0x0,0x180000,0x180000,0xc800000,0x4800000,0x180000,0x180000,0x70c0,0x0,0x0,0x0,0x0,0x0,0x80000000,0x10000000,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x2,0x0,0x0,0x21c,0x4,0x0,0x0,0x218,0x218,0x140,0x140,0x358,0x358,0x0,0x0,0x2,};
   }

  /** Constructor with InputStream. */
  public Shell(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Shell(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ShellTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
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
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public Shell(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ShellTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public Shell(ShellTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ShellTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
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
    boolean[] la1tokens = new boolean[42];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 19; i++) {
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
    for (int i = 0; i < 42; i++) {
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
