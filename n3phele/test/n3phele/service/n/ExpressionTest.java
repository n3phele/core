package n3phele.service.n;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest;
import n3phele.service.model.Action;
import n3phele.service.model.Context;
import n3phele.service.model.ShellFragment;
import n3phele.service.model.SignalKind;
import n3phele.service.nShell.Expression;
import n3phele.service.nShell.ExpressionEngine;
import n3phele.service.nShell.ParseException;
import n3phele.service.nShell.SelfCompilingNode;
import n3phele.service.nShell.UnexpectedTypeException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExpressionTest {
	static Context context;
	static Context contextA;
	static Context contextB;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		context = new Context();
		contextA = new Context();
		contextB = new Context();
		
		contextA.putValue("a0", true);
		contextA.putValue("a1", false);
		contextA.putValue("a2", 4);
		contextA.putValue("a3", -10.0);
		contextA.putValue("a4", "sally");
		
		contextB.putValue("b0", false);
		contextB.putValue("b1", true);
		contextB.putValue("b2", -94);
		contextB.putValue("b3", 13.9);
		contextB.putValue("b4", "field");
		
		Action action = new Action(){

			@Override
			public boolean call() throws WaitForSignalRequest, Exception {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void dump() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void init() throws Exception {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void signal(SignalKind kind, String assertion) {
				// TODO Auto-generated method stub
				
			}};
		action.setContext(contextA);
		action.setUri(URI.create("http://foo/contextA"));
		context.putValue("contextA", action);
		
		Action action2 = new Action(){

			@Override
			public boolean call() throws WaitForSignalRequest, Exception {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void dump() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void init() throws Exception {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void signal(SignalKind kind, String assertion) {
				// TODO Auto-generated method stub
				
			}};
		action2.setContext(contextB);
		action2.setUri(URI.create("http://foo/contextB"));
		context.putValue("contextB", action2);
	}

	@Test
	public void testBasicArithmetic() throws IllegalArgumentException, ParseException, UnexpectedTypeException {

		context.putObjectValue("i1", eval("3+2", context));
		context.putObjectValue("i2", eval("3*2", context));
		context.putObjectValue("i3", eval("6/2", context));
		context.putObjectValue("i4", eval("7%2", context));
		context.putObjectValue("i5", eval("1.0+2", context));
		context.putObjectValue("i6", eval("2.5*2.1", context));
		context.putObjectValue("i7", eval("1+2", context));
		context.putObjectValue("i8", eval("1.0+2.0", context));
		context.putObjectValue("i9", eval("1+2.0", context));
		context.putObjectValue("i10", eval("1.0+2", context));
		context.putObjectValue("i11", eval("1+2", context));
		
		context.putObjectValue("i12", eval("3*2", context));
		context.putObjectValue("i13", eval("3.0*2", context));
		context.putObjectValue("i14", eval("3*2.0", context));
		context.putObjectValue("i15", eval("3.0*2.0", context));
		
		context.putObjectValue("i16", eval("7/2", context));
		context.putObjectValue("i17", eval("7.0/2", context));
		context.putObjectValue("i18", eval("7/2.0", context));
		context.putObjectValue("i19", eval("7.0/2.0", context));
		
		context.putObjectValue("i20", eval("7%2", context));
		context.putObjectValue("i21", eval("7.0%2", context));
		context.putObjectValue("i22", eval("7%2.0", context));
		context.putObjectValue("i23", eval("7.0%2.0", context));
		
		context.putObjectValue("i24", eval("7-2", context));
		context.putObjectValue("i25", eval("7.1-2", context));
		context.putObjectValue("i26", eval("7-2.1", context));
		context.putObjectValue("i27", eval("7.1-2.0", context));
		
		context.putObjectValue("i28", eval("7-2", context));
		context.putObjectValue("i29", eval("7.1-2", context));
		context.putObjectValue("i30", eval("7-2.1", context));
		context.putObjectValue("i31", eval("7.1-2.0", context));
		
		context.putObjectValue("i32", eval("\"7.1-2.0\"", context));
		context.putObjectValue("i33", eval("\"hello\"+\"dolly\"", context));
		context.putObjectValue("i34", eval("$$max(3600*1000, 300)", context));

		
		Assert.assertEquals("5", context.getValue("i1"));
		Assert.assertEquals("6", context.getValue("i2"));
		Assert.assertEquals("3", context.getValue("i3"));
		Assert.assertEquals("1", context.getValue("i4"));
		Assert.assertEquals("3.0", context.getValue("i5"));
		Assert.assertEquals("5.25", context.getValue("i6"));
		Assert.assertEquals("3", context.getValue("i7"));
		Assert.assertEquals("3.0", context.getValue("i8"));
		Assert.assertEquals("3.0", context.getValue("i9"));
		Assert.assertEquals("3.0", context.getValue("i10"));
		Assert.assertEquals("3", context.getValue("i11"));
		
		Assert.assertEquals("6", context.getValue("i12"));
		Assert.assertEquals("6.0", context.getValue("i13"));
		Assert.assertEquals("6.0", context.getValue("i14"));
		Assert.assertEquals("6.0", context.getValue("i15"));
		
		Assert.assertEquals("3", context.getValue("i16"));
		Assert.assertEquals(Long.valueOf(3), context.getObjectValue("i16"));
		Assert.assertEquals("3.5", context.getValue("i17"));
		Assert.assertEquals("3.5", context.getValue("i18"));
		Assert.assertEquals("3.5", context.getValue("i19"));
		
		Assert.assertEquals("1", context.getValue("i20"));
		Assert.assertEquals("1.0", context.getValue("i21"));
		Assert.assertEquals("1.0", context.getValue("i22"));
		Assert.assertEquals("1.0", context.getValue("i22"));
		
		Assert.assertEquals("5", context.getValue("i24"));
		Assert.assertEquals("5.1", context.getValue("i25"));
		Assert.assertEquals(5, context.getIntegerValue("i25"));
		Assert.assertEquals("4.9", context.getValue("i26"));
		Assert.assertEquals(4L, context.getLongValue("i26"));
		Assert.assertEquals("5.1", context.getValue("i27"));
		
		Assert.assertEquals(5.0, context.getDoubleValue("i28"));
		Assert.assertEquals(5.1, context.getDoubleValue("i29"));
		Assert.assertEquals(4.9, context.getDoubleValue("i30"));
		Assert.assertEquals(5.1, context.getDoubleValue("i31"));
		
		Assert.assertEquals("7.1-2.0", context.getValue("i32"));
		Assert.assertEquals("hellodolly", context.getValue("i33"));
		Assert.assertEquals("3600000", context.getValue("i34"));
		
		
	}
	
	@Test
	public void testLogicalArithmetic() throws IllegalArgumentException, ParseException, UnexpectedTypeException {
		
		context.putObjectValue("i0", eval("1 > 2", context));
		context.putObjectValue("i1", eval("1.0 > 2", context));
		context.putObjectValue("i2", eval("1 > 2.0", context));
		context.putObjectValue("i3", eval("2 >= 1", context));
		
		context.putObjectValue("i4", eval("2.0 >= 1", context));
		context.putObjectValue("i5", eval("2.0 >= 1.0", context));
		context.putObjectValue("i6", eval("1 == 1", context));
		context.putObjectValue("i7", eval("1 == 2", context));
		context.putObjectValue("i8", eval("1 != 1", context));
		context.putObjectValue("i9", eval("1 != 2", context));
		context.putObjectValue("i10", eval("1.0 != 1", context));
		context.putObjectValue("i11", eval("1.0 == 1", context));
		context.putObjectValue("i12", eval("1.0 <= 1", context));
		
		context.putObjectValue("i13", eval("1.0 <= 2.0", context));
		context.putObjectValue("i14", eval("\"hello\"==\"hello\"", context));
		context.putObjectValue("i15", eval("\"hello\"==\"hell0\"", context));
		context.putObjectValue("i16", eval("\"hello\"!=\"hello\"", context));
		context.putObjectValue("i17", eval("\"hello\"!=\"hell0\"", context));
		
		context.putObjectValue("i18", eval("true & true", context));
		context.putObjectValue("i19", eval("true & false", context));
		context.putObjectValue("i20", eval("false & false", context));
		context.putObjectValue("i21", eval("false | true", context));
		context.putObjectValue("i22", eval("false | false", context));
		context.putObjectValue("i23", eval("true | true", context));
		context.putObjectValue("i24", eval("true | false", context));
		context.putObjectValue("i25", eval("(true|false) & (!false&true)", context));
		
		Assert.assertEquals("false", context.getValue("i0"));
		Assert.assertEquals("false", context.getValue("i1"));
		Assert.assertEquals("false", context.getValue("i2"));
		Assert.assertEquals("true", context.getValue("i3"));
		
		Assert.assertEquals("true", context.getValue("i4"));
		Assert.assertEquals("true", context.getValue("i5"));
		Assert.assertEquals("true", context.getValue("i6"));
		Assert.assertEquals("false", context.getValue("i7"));
		Assert.assertEquals("false", context.getValue("i8"));
		Assert.assertEquals("true", context.getValue("i9"));
		Assert.assertEquals("false", context.getValue("i10"));
		Assert.assertEquals("true", context.getValue("i11"));
		Assert.assertEquals("true", context.getValue("i12"));
		
		Assert.assertEquals("true", context.getValue("i13"));
		Assert.assertEquals("true", context.getValue("i14"));
		Assert.assertEquals("false", context.getValue("i15"));
		Assert.assertEquals("false", context.getValue("i16"));
		Assert.assertEquals("true", context.getValue("i17"));
		
		Assert.assertEquals("true", context.getValue("i18"));
		Assert.assertEquals("false", context.getValue("i19"));
		Assert.assertEquals("false", context.getValue("i20"));
		Assert.assertEquals("true", context.getValue("i21"));
		Assert.assertEquals("false", context.getValue("i22"));
		Assert.assertEquals("true", context.getValue("i23"));
		Assert.assertEquals("true", context.getValue("i24"));
		Assert.assertEquals("true", context.getValue("i25"));
		
	}
	
	@Test
	public void testUnaryArithmetic() throws IllegalArgumentException, ParseException, UnexpectedTypeException {
		
		context.putObjectValue("i0", eval("!0", context));
		context.putObjectValue("i1", eval("!1", context));
		context.putObjectValue("i2", eval("!0", context));
		context.putObjectValue("i3", eval("!1", context));
		
		context.putObjectValue("i4", eval("!(2+0)", context));
		context.putObjectValue("i5", eval("~(1+0)", context));
		context.putObjectValue("i6", eval("-(5+0)", context));
		context.putObjectValue("i7", eval("+(5+0)", context));
		
		context.putObjectValue("i8", eval("!(2.0+0)", context));
		context.putObjectValue("i9", eval("!(0.0+0)", context));
		context.putObjectValue("i10", eval("~(1.0+0)", context));
		context.putObjectValue("i11", eval("-(5.0+0)", context));
		context.putObjectValue("i12", eval("+(5.0+0)", context));
		
		
		context.putObjectValue("i13", eval("0x10", context));
		context.putObjectValue("i14", eval("-0x10", context));
		context.putObjectValue("i15", eval("010", context));
		context.putObjectValue("i16", eval("-010", context));
		
		
		Assert.assertEquals("true", context.getValue("i0"));
		Assert.assertEquals("false", context.getValue("i1"));
		Assert.assertEquals("true", context.getValue("i2"));
		Assert.assertEquals("false", context.getValue("i3"));
		
		Assert.assertEquals("false", context.getValue("i4"));
		Assert.assertEquals("-2", context.getValue("i5"));
		Assert.assertEquals("-5", context.getValue("i6"));
		Assert.assertEquals("5", context.getValue("i7"));
		Assert.assertEquals("false", context.getValue("i8"));
		Assert.assertEquals("true", context.getValue("i9"));
		Assert.assertEquals("-2", context.getValue("i10"));
		Assert.assertEquals("-5.0", context.getValue("i11"));
		Assert.assertEquals("5.0", context.getValue("i12"));
		
		Assert.assertEquals("16", context.getValue("i13"));
		Assert.assertEquals("-16", context.getValue("i14"));
		Assert.assertEquals("8", context.getValue("i15"));
		Assert.assertEquals("-8", context.getValue("i16"));
		
		
	}
	
	@Test
	public void testVariables() throws IllegalArgumentException, ParseException, UnexpectedTypeException {
		context.putAll(contextA);
		context.putAll(contextB);
		
		context.putObjectValue("i0", eval("$$a4+$$b4", context));
		context.putObjectValue("i1", eval( "$$a0&$$b1", context));
		context.putObjectValue("i2", eval("$$a2+$$b2", context));
		context.putObjectValue("i3", eval( "$$a3*$$b3", context));
		
		context.putObjectValue("i4", eval( "$$contextA.a0", context));
		context.putObjectValue("i5", eval( "$$contextA.a1", context));
		context.putObjectValue("i6", eval("$$contextA.a2", context));
		context.putObjectValue("i7", eval("$$contextA.a3", context));
		context.putObjectValue("i8", eval("$$contextA.a4", context));
		
		context.putObjectValue("i9", eval("$$b0", context));
		context.putObjectValue("i10", eval( "$$b1", context));
		context.putObjectValue("i11", eval("$$b2", context));
		context.putObjectValue("i12", eval("$$b3", context));
		context.putObjectValue("i13", eval("$$b4", context));
		
		context.putObjectValue("i14", eval("($$b3*3)+$$i12", context));
		context.putObjectValue("i15", eval("$$b3*3+$$i12", context));

		
		Assert.assertEquals("sallyfield", context.getValue("i0"));
		Assert.assertEquals("true", context.getValue("i1"));
		Assert.assertEquals("-90", context.getValue("i2"));
		Assert.assertEquals("-139.0", context.getValue("i3"));
		
		Assert.assertEquals("true", context.getValue("i4"));
		Assert.assertEquals("false", context.getValue("i5"));
		Assert.assertEquals("4", context.getValue("i6"));
		Assert.assertEquals("-10.0", context.getValue("i7"));
		Assert.assertEquals("sally", context.getValue("i8"));
		
		Assert.assertEquals("false", context.getValue("i9"));
		Assert.assertEquals("true", context.getValue("i10"));
		Assert.assertEquals("-94", context.getValue("i11"));
		Assert.assertEquals("13.9", context.getValue("i12"));
		Assert.assertEquals("field", context.getValue("i13"));
		
		Assert.assertEquals((13.9*3)+13.9, context.getDoubleValue("i14"));
		Assert.assertEquals(13.9*3+13.9, context.getDoubleValue("i15"));
		
	}
	
	@Test
	public void testConditionals() throws IllegalArgumentException, ParseException, UnexpectedTypeException {
		
		context.putObjectValue("i0", eval( "(1==1)?\"yes\":\"no\"", context));
		context.putObjectValue("i1", eval( "(1==0)?\"yes\":\"no\"", context));
		context.putObjectValue("i2", eval( "(1==1)?3:-5", context));
		context.putObjectValue("i3", eval( "(1==0)?3:-5", context));
		context.putObjectValue("i4", eval( "(1==1)?1.2:4.3", context));
		context.putObjectValue("i5", eval( "(1==0)?1.2:4.3", context));

		
		Assert.assertEquals("yes", context.getValue("i0"));
		Assert.assertEquals("no", context.getValue("i1"));
		Assert.assertEquals("3", context.getValue("i2"));
		Assert.assertEquals("-5", context.getValue("i3"));
		
		Assert.assertEquals("1.2", context.getValue("i4"));
		Assert.assertEquals("4.3", context.getValue("i5"));
		
		
	}
	
	@Test
	public void testFunctions() throws IllegalArgumentException, ParseException, UnexpectedTypeException {
		
		
		context.putObjectValue("i0", eval( "$$regex(\"foo\", \"f(.)o\",1)", context));
		context.putObjectValue("i1", eval( "$$regex(\"Foo\", \"f(.)o\",1)", context));
		context.putObjectValue("i2", eval( "$$regex(\"foo\", \"f(.)o\",1)", context));
		context.putObjectValue("i3", eval( "$$max(3,4)",context));
		context.putObjectValue("i4", eval( "$$max(4,3)",context));
		context.putObjectValue("i5", eval( "$$min(3,4)",context));
		context.putObjectValue("i6", eval( "$$min(4,3)",context));
		context.putObjectValue("i7", eval( "$$max(3.0,4.0)",context));
		context.putObjectValue("i8", eval( "$$max(4,3.0)",context));
		context.putObjectValue("i9", eval("$$min(3.3,4)",context));
		context.putObjectValue("i10", eval( "$$min(4,3.1)",context));
		context.putObjectValue("i11", eval( "$$max(3.0,4.0)",context));
		context.putObjectValue("i12", eval( "$$max(4,3.0)",context));
		context.putObjectValue("i13", eval( "$$min(3.3,4)",context));
		context.putObjectValue("i14", eval( "$$min(4,3.1)",context));
		context.putObjectValue("i15", eval("$$min(4,3.1)+$$min(1,2)",context));
		context.putObjectValue("i16", eval( "$$length(\"foo\")",context));

		
		Assert.assertEquals("o", context.getValue("i0"));
		Assert.assertEquals("", context.getValue("i1"));
		Assert.assertEquals("o", context.getValue("i2"));
		Assert.assertEquals("4", context.getValue("i3"));
		Assert.assertEquals("4", context.getValue("i4"));
		Assert.assertEquals("3", context.getValue("i5"));
		Assert.assertEquals("3", context.getValue("i6"));
		Assert.assertEquals("4.0", context.getValue("i7"));
		Assert.assertEquals("4.0", context.getValue("i8"));
		Assert.assertEquals("3.3", context.getValue("i9"));
		Assert.assertEquals("3.1", context.getValue("i10"));
		Assert.assertEquals("4.0", context.getValue("i11"));
		Assert.assertEquals("4.0", context.getValue("i12"));
		Assert.assertEquals("3.3", context.getValue("i13"));
		Assert.assertEquals("3.1", context.getValue("i14"));
		Assert.assertEquals("4.1", context.getValue("i15"));
		Assert.assertEquals("3", context.getValue("i16"));
		
		
	}


	
	@Test
	public void testList() throws IllegalArgumentException, ParseException, UnexpectedTypeException {
		
		context.putObjectValue("i0", Arrays.asList("larry", "moe", "curly"));
		context.putObjectValue("nullList", Arrays.asList());
		context.putObjectValue("i1", eval( "$$length($$nullList)", context));
		context.putObjectValue("i2", eval( "$$length($$i0)", context));
		context.putObjectValue("i3", eval( "$$length($$i0+$$nullList)", context));
		context.putObjectValue("i4", eval( "$$length($$i0+$$i0)", context));
		context.putObjectValue("i5", eval( "$$i0[0]==\"larry\"", context));
		context.putObjectValue("i6", eval( "$$i0[0,0]==$$nullList", context));
		context.putObjectValue("i7", eval("($$i0+$$i0)[1]", context));
		context.putObjectValue("i8", eval( "($$i0+$$i0+$$i0)[1,2]", context));
		context.putObjectValue("i9", eval( "((4*9)+(5*3))/2+1", context));
		context.putObjectValue("i10", eval( "($$string($$i0+$$i0, \"=\", \"{}\"))", context));
		context.putObjectValue("i11", eval( "($$string($$i0+$$i0, \", \", \"\"))", context));
		
		Assert.assertEquals("[larry, moe, curly]", context.getValue("i0"));
		Assert.assertEquals(0, context.getIntegerValue("i1"));
		Assert.assertEquals(3, context.getIntegerValue("i2"));
		Assert.assertEquals("3", context.getValue("i3"));		
		Assert.assertEquals("6", context.getValue("i4"));
		Assert.assertEquals(true, context.getBooleanValue("i5"));
		Assert.assertEquals("true", context.getValue("i6"));
		Assert.assertEquals("moe", context.getValue("i7"));
		Assert.assertEquals("[moe]", context.getValue("i8"));
		Assert.assertEquals("26", context.getValue("i9"));
		Assert.assertEquals("{larry}={moe}={curly}={larry}={moe}={curly}", context.getValue("i10"));
		Assert.assertEquals("larry, moe, curly, larry, moe, curly", context.getValue("i11"));

	}
	
	/*
	 * Helpers
	 */

	private Object eval(String s, Context context) throws ParseException, IllegalArgumentException, UnexpectedTypeException {
		Expression exp = new Expression(s, 0, 0);
		SelfCompilingNode node = exp.buildExpressionTree();
		List<ShellFragment> result = new ArrayList<ShellFragment>();
		node.compile(result);
		ExpressionEngine ee = new TestExpressionEngine(result, context);
		return ee.run();
	}
	
	private class TestExpressionEngine extends ExpressionEngine {

		public TestExpressionEngine(List<ShellFragment> executable,
				Context context) {
			super(executable, context);
		}
		
		/* (non-Javadoc)
		 * @see n3phele.service.nShell.ExpressionEngine#getContext(java.net.URI)
		 */
		protected Context getContext(URI uri) throws NotFoundException {
			if(uri.equals(URI.create("http://foo/contextA"))) {
				return contextA;
			} else if(uri.equals(URI.create("http://foo/contextA"))) {
				return contextA;
			}
			throw new NotFoundException();
		}
		
	}
}
