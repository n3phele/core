/* Generated By:JJTree&JavaCC: Do not edit this line. ExpressionTokenManager.java */
package n3phele.service.nShell;

/** Token Manager. */
public class ExpressionTokenManager implements ExpressionConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x3fc0L) != 0L)
            return 19;
         return -1;
      case 1:
         if ((active0 & 0x3fc0L) != 0L)
            return 20;
         return -1;
      case 2:
         if ((active0 & 0x3fc0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 2;
            return 21;
         }
         return -1;
      case 3:
         if ((active0 & 0x3fc0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 3;
            return 21;
         }
         return -1;
      case 4:
         if ((active0 & 0x3fc0L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 4;
            return 21;
         }
         return -1;
      case 5:
         if ((active0 & 0x3e40L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 5;
            return 21;
         }
         return -1;
      case 6:
         if ((active0 & 0x1e40L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 6;
            return 21;
         }
         return -1;
      case 7:
         if ((active0 & 0x1e00L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 7;
            return 21;
         }
         return -1;
      case 8:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 8;
            return 21;
         }
         return -1;
      case 9:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 9;
            return 21;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         jjmatchedKind = 45;
         return jjMoveStringLiteralDfa1_0(0x400000000L);
      case 36:
         return jjMoveStringLiteralDfa1_0(0x3fc0L);
      case 37:
         return jjStopAtPos(0, 43);
      case 38:
         return jjStopAtPos(0, 32);
      case 40:
         return jjStopAtPos(0, 46);
      case 41:
         return jjStopAtPos(0, 28);
      case 42:
         return jjStopAtPos(0, 41);
      case 43:
         return jjStopAtPos(0, 39);
      case 44:
         return jjStopAtPos(0, 27);
      case 45:
         return jjStopAtPos(0, 40);
      case 47:
         return jjStopAtPos(0, 42);
      case 58:
         return jjStopAtPos(0, 30);
      case 60:
         jjmatchedKind = 35;
         return jjMoveStringLiteralDfa1_0(0x2000000000L);
      case 61:
         return jjMoveStringLiteralDfa1_0(0x200000000L);
      case 62:
         jjmatchedKind = 36;
         return jjMoveStringLiteralDfa1_0(0x4000000000L);
      case 63:
         return jjStopAtPos(0, 29);
      case 91:
         return jjStopAtPos(0, 47);
      case 93:
         return jjStopAtPos(0, 48);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x8000L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x4000L);
      case 124:
         return jjStopAtPos(0, 31);
      case 126:
         return jjStopAtPos(0, 44);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 36:
         return jjMoveStringLiteralDfa2_0(active0, 0x3fc0L);
      case 61:
         if ((active0 & 0x200000000L) != 0L)
            return jjStopAtPos(1, 33);
         else if ((active0 & 0x400000000L) != 0L)
            return jjStopAtPos(1, 34);
         else if ((active0 & 0x2000000000L) != 0L)
            return jjStopAtPos(1, 37);
         else if ((active0 & 0x4000000000L) != 0L)
            return jjStopAtPos(1, 38);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x8000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x400L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0xa200L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x180L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x40L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x4800L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x80L);
      case 101:
         if ((active0 & 0x4000L) != 0L)
            return jjStopAtPos(3, 14);
         return jjMoveStringLiteralDfa4_0(active0, 0x240L);
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x2100L);
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x800L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x8400L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x400L);
      case 101:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(4, 15);
         return jjMoveStringLiteralDfa5_0(active0, 0x800L);
      case 103:
         return jjMoveStringLiteralDfa5_0(active0, 0x40L);
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x300L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000L);
      case 115:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000L);
      case 120:
         return jjMoveStringLiteralDfa5_0(active0, 0x80L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x80L) != 0L)
            return jjStopAtPos(5, 7);
         else if ((active0 & 0x100L) != 0L)
            return jjStopAtPos(5, 8);
         break;
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x400L);
      case 101:
         return jjMoveStringLiteralDfa6_0(active0, 0x40L);
      case 103:
         return jjMoveStringLiteralDfa6_0(active0, 0x200L);
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x1000L);
      case 115:
         return jjMoveStringLiteralDfa6_0(active0, 0x800L);
      case 116:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x2000L) != 0L)
            return jjStopAtPos(6, 13);
         break;
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x800L);
      case 110:
         return jjMoveStringLiteralDfa7_0(active0, 0x1000L);
      case 112:
         return jjMoveStringLiteralDfa7_0(active0, 0x400L);
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0x200L);
      case 120:
         return jjMoveStringLiteralDfa7_0(active0, 0x40L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x40L) != 0L)
            return jjStopAtPos(7, 6);
         break;
      case 97:
         return jjMoveStringLiteralDfa8_0(active0, 0x800L);
      case 101:
         return jjMoveStringLiteralDfa8_0(active0, 0x400L);
      case 103:
         return jjMoveStringLiteralDfa8_0(active0, 0x1000L);
      case 104:
         return jjMoveStringLiteralDfa8_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x200L) != 0L)
            return jjStopAtPos(8, 9);
         else if ((active0 & 0x400L) != 0L)
            return jjStopAtPos(8, 10);
         else if ((active0 & 0x1000L) != 0L)
            return jjStopAtPos(8, 12);
         break;
      case 112:
         return jjMoveStringLiteralDfa9_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa10_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x800L) != 0L)
            return jjStopAtPos(10, 11);
         break;
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 42;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 6);
                  else if (curChar == 36)
                     jjstateSet[jjnewStateCnt++] = 19;
                  else if (curChar == 34)
                     jjCheckNAddStates(7, 9);
                  else if (curChar == 46)
                     jjCheckNAdd(5);
                  else if (curChar == 35)
                  {
                     if (kind > 5)
                        kind = 5;
                     jjCheckNAdd(1);
                  }
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 16)
                        kind = 16;
                     jjCheckNAdd(3);
                  }
                  else if (curChar == 48)
                  {
                     if (kind > 16)
                        kind = 16;
                     jjCheckNAddTwoStates(39, 41);
                  }
                  break;
               case 1:
                  if ((0xfffffffffffffbffL & l) == 0L)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAdd(1);
                  break;
               case 2:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAdd(3);
                  break;
               case 3:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAdd(3);
                  break;
               case 4:
                  if (curChar == 46)
                     jjCheckNAdd(5);
                  break;
               case 5:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddStates(10, 12);
                  break;
               case 7:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(8);
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddTwoStates(8, 9);
                  break;
               case 10:
                  if (curChar == 34)
                     jjCheckNAddStates(7, 9);
                  break;
               case 11:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     jjCheckNAddStates(7, 9);
                  break;
               case 13:
                  if ((0x8400000000L & l) != 0L)
                     jjCheckNAddStates(7, 9);
                  break;
               case 14:
                  if (curChar == 34 && kind > 22)
                     kind = 22;
                  break;
               case 15:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(13, 16);
                  break;
               case 16:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(7, 9);
                  break;
               case 17:
                  if ((0xf000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 18:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(16);
                  break;
               case 19:
                  if (curChar == 36)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 21:
                  if ((0x3ff400000000000L & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjstateSet[jjnewStateCnt++] = 21;
                  break;
               case 22:
                  if (curChar == 36)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 23:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 6);
                  break;
               case 24:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(24, 25);
                  break;
               case 25:
                  if (curChar != 46)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddStates(17, 19);
                  break;
               case 26:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddStates(17, 19);
                  break;
               case 28:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(29);
                  break;
               case 29:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddTwoStates(29, 9);
                  break;
               case 30:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(30, 31);
                  break;
               case 32:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(33);
                  break;
               case 33:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddTwoStates(33, 9);
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(20, 22);
                  break;
               case 36:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(37);
                  break;
               case 37:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(37, 9);
                  break;
               case 38:
                  if (curChar != 48)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAddTwoStates(39, 41);
                  break;
               case 40:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjstateSet[jjnewStateCnt++] = 40;
                  break;
               case 41:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAdd(41);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if (kind > 5)
                     kind = 5;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 6:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(23, 24);
                  break;
               case 9:
                  if ((0x5000000050L & l) != 0L && kind > 20)
                     kind = 20;
                  break;
               case 11:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(7, 9);
                  break;
               case 12:
                  if (curChar == 92)
                     jjAddStates(25, 27);
                  break;
               case 13:
                  if ((0x14404410000000L & l) != 0L)
                     jjCheckNAddStates(7, 9);
                  break;
               case 20:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjCheckNAdd(21);
                  break;
               case 21:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjCheckNAdd(21);
                  break;
               case 27:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(28, 29);
                  break;
               case 31:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(30, 31);
                  break;
               case 35:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(32, 33);
                  break;
               case 39:
                  if ((0x100000001000000L & l) != 0L)
                     jjCheckNAdd(40);
                  break;
               case 40:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAdd(40);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 11:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(7, 9);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 42 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   24, 25, 30, 31, 34, 35, 9, 11, 12, 14, 5, 6, 9, 11, 12, 16, 
   14, 26, 27, 9, 34, 35, 9, 7, 8, 13, 15, 17, 28, 29, 32, 33, 
   36, 37, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, "\44\44\162\145\147\145\170\50", 
"\44\44\155\141\170\50", "\44\44\155\151\156\50", "\44\44\154\145\156\147\164\150\50", 
"\44\44\145\163\143\141\160\145\50", "\44\44\165\156\145\163\143\141\160\145\50", 
"\44\44\163\164\162\151\156\147\50", "\44\44\154\151\163\164\50", "\164\162\165\145", "\146\141\154\163\145", null, 
null, null, null, null, null, null, null, null, null, null, "\54", "\51", "\77", 
"\72", "\174", "\46", "\75\75", "\41\75", "\74", "\76", "\74\75", "\76\75", "\53", 
"\55", "\52", "\57", "\45", "\176", "\41", "\50", "\133", "\135", };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0x1fffff8d1ffc1L, 
};
static final long[] jjtoSkip = {
   0x3eL, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[42];
private final int[] jjstateSet = new int[84];
protected char curChar;
/** Constructor. */
public ExpressionTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public ExpressionTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 42; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
