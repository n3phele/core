package n3phele.service.nShell;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.Context;
import n3phele.service.model.ShellFragment;
import n3phele.service.model.Variable;
import n3phele.service.n.helpers.StringEscapeUtils;

public class ExpressionEngine {
	private List<ShellFragment> executable;
	private Context context;
	
	
	public ExpressionEngine(List<ShellFragment> executable, Context context) {
		this.executable = executable;
		this.context = context;
	}

	Variable eval(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		return (Variable) eval(this.executable.indexOf(s));
	}
	
	Object eval(int pc) throws UnexpectedTypeException, IllegalArgumentException {
		ShellFragment s = executable.get(pc);
		switch (s.kind){
		case additiveExpression:
			return additiveExpression(s);
		case conditionalExpression:
			return conditionalExpression(s);
		case constantLong:
			return constantLong(s);
		case constantDouble:
			return constantDouble(s);
		case constantString:
			return constantString(s);
		case equalityExpression:
			return equalityExpression(s);
		case functionExpression:
			return functionExpression(s);
		case identifier:
			return identifier(s);
		case list:
			return list(s);
		case logicalANDExpression:
			return logicalANDExpression(s);
		case logicalORExpression:
			return logicalORExpression(s);
		case multiplicativeExpression:
			return multiplicativeExpression(s);
		case relationalExpression:
			return relationalExpression(s);
		case subList:
			return subList(s);
		case unaryExpression:
			return unaryExpression(s);
		default:
			break;
		
		}
		return s;
		
	}
	
	/*
	 * Expression processing
	 * =====================
	 */



	private Object unaryExpression(ShellFragment s) throws UnexpectedTypeException {
		Object[] stack;
		if((stack = oneChild(s)) != null) {
			if(s.value == null || s.value.equals("+")) {
				return stack[0];
			} else if(s.value.equals("-")) {
				if(stack[0] instanceof Long) {
					return -(Long) stack[0];
				} else if(stack[0] instanceof Double) {
					return - (Double) stack[0];
				} else
					throw new UnexpectedTypeException(stack[0], "numeric");
			} else if(s.value.equals("!")) {
				if(stack[0] instanceof Long) {
					return Boolean.valueOf(((Long) stack[0]) == 0L);
				} else if(stack[0] instanceof Double) {
					return Boolean.valueOf(((Double) stack[0]) == 0);
				} else if(stack[0] instanceof Boolean) {
					return ! (Boolean) stack[0];
				} else
					throw new UnexpectedTypeException(stack[0], "numeric or boolean");
			} else if(s.value.equals("~")) {
				if(stack[0] instanceof Long) {
					return ~(Long) stack[0];
				} else if(stack[0] instanceof Double) {
					return ~Long.valueOf(((Double) stack[0]).longValue());
				} else 
					throw new UnexpectedTypeException(stack[0], "numeric");
			}

			
		} else {
			stack = twoChildren(s);
			if(isClass(stack[0], "List", List.class) && isClass(stack[1], "integer", Long.class))
				return ((List<?>)stack[0]).get(((Long)stack[1]).intValue());
		}
		
		throw new IllegalArgumentException();
	}


	private Object subList(ShellFragment s) throws UnexpectedTypeException {
		Object[] stack;
		if((stack = threeChildren(s))!=null) {
			if(isClass(stack[0], "List", List.class) && isClass(stack[1], "integer", Long.class) && isClass(stack[2], "integer", Long.class))
				return ((List<?>)stack[0]).subList(((Long)stack[1]).intValue(), ((Long)stack[2]).intValue());
		} else if((stack = oneChild(s))!=null) {
			return stack[0];
		} 
		throw new IllegalArgumentException();
	}

	private Object equalityExpression(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		Object stack[];
		if((stack = twoChildren(s))!=null) {
			if(s.value.equals("==")) {
				return stack[0].equals(stack[1]);
			} else if(s.value.equals("!=")) {
				return !stack[0].equals(stack[1]);
			}
		} else if((stack = oneChild(s))!=null) {
			return stack[0];
		}
		throw new IllegalArgumentException();
	}

	private Object relationalExpression(ShellFragment s) throws UnexpectedTypeException {
		Object stack[];

		if((stack = twoChildren(s))!=null) {
			if(s.value.equals(">")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Boolean.valueOf(((Long)stack[0])>((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Boolean.valueOf((Double.valueOf((Long)stack[0]))>((Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Boolean.valueOf(((Double)stack[0])>((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Boolean.valueOf(((Double)stack[0])>(Double.valueOf((Long)stack[1])));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			} else if(s.value.equals(">=")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Boolean.valueOf(((Long)stack[0])>=((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Boolean.valueOf((Double.valueOf((Long)stack[0]))>=((Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Boolean.valueOf(((Double)stack[0])>=((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Boolean.valueOf(((Double)stack[0])>=(Double.valueOf((Long)stack[1])));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			} else if(s.value.equals("<")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Boolean.valueOf(((Long)stack[0])<((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Boolean.valueOf((Double.valueOf((Long)stack[0]))<((Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Boolean.valueOf(((Double)stack[0])<((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Boolean.valueOf(((Double)stack[0])<(Double.valueOf((Long)stack[1])));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			} else if(s.value.equals("<=")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Boolean.valueOf(((Long)stack[0])<=((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Boolean.valueOf((Double.valueOf((Long)stack[0]))<=((Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Boolean.valueOf(((Double)stack[0])<=((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Boolean.valueOf(((Double)stack[0])<=(Double.valueOf((Long)stack[1])));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			}
			throw new UnexpectedTypeException(stack[0], "numeric");
		} else if((stack = oneChild(s))!=null) {
			return stack[0];
		}
		throw new IllegalArgumentException();
	}



	private Object multiplicativeExpression(ShellFragment s) throws UnexpectedTypeException {
		Object stack[];
		if((stack = twoChildren(s))!=null) {
			if(s.value.equals("*")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Long.valueOf(((Long)stack[0])*((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Double.valueOf((Double.valueOf((Long)stack[0]))*((Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Double.valueOf(((Double)stack[0])*((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Double.valueOf(((Double)stack[0])*(Double.valueOf((Long)stack[1])));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			} else if(s.value.equals("/")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Long.valueOf(((Long)stack[0])/((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Double.valueOf((Double.valueOf((Long)stack[0]))/((Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Double.valueOf(((Double)stack[0])/((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Double.valueOf(((Double)stack[0])/(Double.valueOf((Long)stack[1])));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			} else if(s.value.equals("%")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Long.valueOf(((Long)stack[0])%((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Double.valueOf(((Long)stack[0]%(Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Double.valueOf(((Double)stack[0])%((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Double.valueOf(((Double)stack[0])%(Long)stack[1]);
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			}
			throw new UnexpectedTypeException(stack[0], "numeric");
		} else if((stack = oneChild(s))!=null) {
			return stack[0];
		}
		throw new IllegalArgumentException();
	}

	private Object logicalORExpression(ShellFragment s) throws UnexpectedTypeException {
		Object stack[];

		if((stack = twoChildren(s))!=null) {

				if(stack[0] instanceof Boolean) {
					if(stack[1] instanceof Boolean) {
						return Boolean.valueOf(((Boolean)stack[0]) || ((Boolean)stack[1]));
					} 
				}
				throw new UnexpectedTypeException(stack[1], "boolean");
		
		} else if((stack = oneChild(s))!=null) {
			return stack[0];
		}
		throw new IllegalArgumentException();
	}

	private Object logicalANDExpression(ShellFragment s) throws UnexpectedTypeException {
		Object stack[];
		if((stack = twoChildren(s))!=null) {

				if(stack[0] instanceof Boolean) {
					if(stack[1] instanceof Boolean) {
						return Boolean.valueOf(((Boolean)stack[0]) && ((Boolean)stack[1]));
					} 
				}
				throw new UnexpectedTypeException(stack[1], "boolean");
		
		} else if((stack = oneChild(s))!=null) {
			return stack[0];
		}
		throw new IllegalArgumentException();
	}

	private Object list(ShellFragment s) throws UnexpectedTypeException {
		Object[] stack;
		if((stack = threeChildren(s))!=null) {
			if(isClass(stack[0], "List", List.class) && isClass(stack[1], "List", List.class)) {
				@SuppressWarnings("unchecked")
				List<String> list1 = (List<String>) stack[0];
				@SuppressWarnings("unchecked")
				List<String> list2 = (List<String>) stack[1];
				list1.addAll(list2);
				return list1;
			}
		} else if((stack = oneChild(s))!=null) {
			return stack[0];
		}
		throw new IllegalArgumentException();
	}

	@SuppressWarnings("unchecked")
	private Object functionExpression(ShellFragment s) throws UnexpectedTypeException {
		/*
		 * (  < REGEX > conditionalExpression() "," conditionalExpression() "," conditionalExpression() ")" { jjtThis.jjtSetValue("regex"); }	
		 *  | < MAX > conditionalExpression() "," conditionalExpression() ")" { jjtThis.jjtSetValue("max"); }
		 *  | < MIN > conditionalExpression() "," conditionalExpression() ")" { jjtThis.jjtSetValue("min"); }
		 *  | < LENGTH > (LOOKAHEAD(list()) list() | conditionalExpression()) ")" { jjtThis.jjtSetValue("length"); }
	     *  | < STRING > list() "," conditionalExpression() "," conditionalExpression() ")" { jjtThis.jjtSetValue("string"); }
		 *  | < ESCAPE > conditionalExpression() ")" { jjtThis.jjtSetValue("escape"); }
		 *  | < UNESCAPE > conditionalExpression() ")" { jjtThis.jjtSetValue("unescape"); }
		 * )
		 */
		Object[] stack;
		Object o;
		if(s.value.equals("regex")) {
			if((stack = threeChildren(s))!=null) { 
				Object target = stack[0];
				Object pattern = stack[1];
				Object field = stack[2];
				if(target instanceof Long)
					target = ((Integer)target).toString();
				else if(target instanceof Double)
					target = ((Double)target).toString();
				
				if(pattern instanceof Long)
					pattern = ((Integer)pattern).toString();
				else if(pattern instanceof Double)
					pattern = ((Double)pattern).toString();
				
				int selector = 0;
				if(field instanceof Double)
					selector = (int) ((Double)field).longValue();
				else if(field instanceof Long)
					selector = ((Long)field).intValue();
				else if(field instanceof String)
					selector = Integer.valueOf((String)field);
				try {	
	                Pattern p = Pattern.compile((String) pattern);
	                Matcher matcher = p.matcher((String)target);
	                matcher.find();
	                o = matcher.group(selector);
				}  catch (IllegalStateException e) { 
	                        	o = ""; 
	            } catch (Exception e) {
					throw new IllegalArgumentException(e.getMessage());
				}

				return o; 
			}
		} else if(s.value.equals("max")) {
			if((stack = twoChildren(s))!=null) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Long.valueOf(Math.max(((Long)stack[0]),((Long)stack[1])));
					} else if(stack[1] instanceof Double) {
						return Double.valueOf(Math.max(Double.valueOf((Long)stack[0]),(Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Double.valueOf(Math.max((Double)stack[0],((Double)stack[1])));
					} else if(stack[1] instanceof Long) {
						return Double.valueOf(Math.max((Double)stack[0], (double)(Long)stack[1]));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			}
			
		} else if(s.value.equals("min")) {
			if((stack = twoChildren(s))!=null) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Long.valueOf(Math.min(((Long)stack[0]),((Long)stack[1])));
					} else if(stack[1] instanceof Double) {
						return Double.valueOf(Math.min(Double.valueOf((Long)stack[0]),(Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Double.valueOf(Math.min((Double)stack[0],((Double)stack[1])));
					} else if(stack[1] instanceof Long) {
						return Double.valueOf(Math.min((Double)stack[0], (double)(Long)stack[1]));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			}
			
		} else if(s.value.equals("length")) {
			if((stack = oneChild(s))!=null) {
				if(stack[0] instanceof String) {
					return Long.valueOf(((String)stack[0]).length());
				} else if(stack[0] instanceof List) {
					return Long.valueOf(((List<?>)stack[0]).size());
				} else
					throw new UnexpectedTypeException(stack[1], "list or string");
			}
			
		} else if(s.value.equals("string")) {
			if((stack = threeChildren(s))!=null) {
				if(stack[0] instanceof List)
				return listToString((List<? extends Object>)stack[0], stack[1], stack[2]);
			}
		} else if(s.value.equals("escape")) {
			if((stack = oneChild(s))!=null && isClass(stack[0], "String", String.class)) {
				return StringEscapeUtils.escapeJavaString((String)stack[0]);
			}
		} else if(s.value.equals("unescape")) {
			if((stack = oneChild(s))!=null && isClass(stack[0], "String", String.class)) {
				return StringEscapeUtils.unescapeJavaString((String)stack[0]);
			}
		}
		throw new IllegalArgumentException(s.value);
	}
	
	private static String listToString(List<? extends Object> o, Object seperatorField, Object quotesField) {
	    StringBuffer b = new StringBuffer();
	    String seperator = " ";
	    char leftQuote = 0;
	    char rightQuote = 0;
	    if(seperatorField instanceof String) {
	            seperator = (String)seperatorField;
	    }
	    if(quotesField instanceof String) {
	            String target = (String)quotesField;
	            if(target!= null && target.length() > 0)
	                    leftQuote = target.charAt(0);
	            if(target!= null && target.length() > 1)
	                    rightQuote = target.charAt(1);
	    }
	
	    int i = 0;
	    for(Object obj : o) {
	            if(i++ > 0)
	                    b.append(seperator);
	            if(obj instanceof String) {
	                    if(leftQuote != 0) b.append(leftQuote);
	                    b.append((String)obj);
	                    if(rightQuote != 0) b.append(rightQuote);
	            } else {
	            		if(leftQuote != 0) b.append(leftQuote);
	                    b.append(String.valueOf(obj));
	                    if(rightQuote != 0) b.append(rightQuote);
	            }
	    }

    	return b.toString();

    }

	
	private Object additiveExpression(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		Object stack[];

		if((stack = twoChildren(s))!=null) {
			if(s.value.equals("+")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Long.valueOf(((Long)stack[0])+((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Double.valueOf((Double.valueOf((Long)stack[0]))+((Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Double.valueOf(((Double)stack[0])+((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Double.valueOf(((Double)stack[0])+(Double.valueOf((Long)stack[1])));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			} else if(s.value.equals("-")) {
				if(stack[0] instanceof Long) {
					if(stack[1] instanceof Long) {
						return Long.valueOf(((Long)stack[0])-((Long)stack[1]));
					} else if(stack[1] instanceof Double) {
						return Double.valueOf((Double.valueOf((Long)stack[0]))-((Double)stack[1]));
					}
				} else if(stack[0] instanceof Double) {
					if(stack[1] instanceof Double) {
						return Double.valueOf(((Double)stack[0])-((Double)stack[1]));
					} else if(stack[1] instanceof Long) {
						return Double.valueOf(((Double)stack[0])-(Double.valueOf((Long)stack[1])));
					} 
				} else
					throw new UnexpectedTypeException(stack[1], "numeric");
			}
			throw new UnexpectedTypeException(stack[0], "numeric");
		} else if((stack = oneChild(s))!=null) {
			return stack[0];
		}
		throw new IllegalArgumentException();
	}
	
	private Object conditionalExpression(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		Object[] stack;
		if((stack = threeChildren(s))!=null) {
			boolean test = false;
			if(isClass(stack[0], "Boolean", Boolean.class)) {
				test = (Boolean) stack[0];
			}
			if(test) {
				return stack[1];
			} else {
				return stack[2];
			}
		} else if((stack = oneChild(s))!= null) {
			return stack[0];
		}
		throw new IllegalArgumentException();
	}

	private Object constantLong(ShellFragment s) {
		return Long.valueOf(s.value);
	}
	
	private Object constantDouble(ShellFragment s) {
		return Double.valueOf(s.value);
	}
	
	private Object constantString(ShellFragment s) {
		return s.value;
	}
	
	private Object identifier(ShellFragment s) {
		Variable v = lookup(s.value);
		if(v == null)
			throw new NotFoundException();
		return v.getExpressionObject();
	}
	


	/*
	 * Helpers
	 * =======
	 */
	private Object[] oneChild(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		if(s.children.size() == 1) {
			return new Object[] { eval(s.children.get(0)) };
		}
		return null;
	}
	
	private Object[] twoChildren(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		if(s.children.size() == 2) {
			return new Object[] { eval(s.children.get(0)), eval(s.children.get(1)) };
		}
		return null;
	}
	
	private Object[] threeChildren(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		if(s.children.size() == 3) {
			return new Object[] { eval(s.children.get(0)), eval(s.children.get(1)), eval(s.children.get(2)) };
		}
		return null;
	}
	
	private boolean isClass(Object o, String expected, Class<?> clazz ) throws UnexpectedTypeException {
		if(o.getClass() == clazz)
			return true;
		throw new UnexpectedTypeException(o, expected);
	}
	
	private Variable lookup(String name) {
		Variable result = this.context.get(name);
		return result;
	}
}
