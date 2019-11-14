import org.junit.Test;

import static org.junit.Assert.*;

public class CalcTest {
    @Test
    public void eval() {
        Calc calc = new Calc();

        assertEquals(Double.doubleToLongBits(11.0), Double.doubleToLongBits(calc.eval("1+2*3+4")));
        assertEquals(Double.doubleToLongBits(21.0), Double.doubleToLongBits(calc.eval("(1+2)*(3+4)")));
        assertEquals(Double.doubleToLongBits(2.0),
                Double.doubleToLongBits(Math.round(calc.eval("sqrt(2)*sqrt(2)") * 100.0) / 100.0));
        assertEquals(Double.doubleToLongBits(3.14159265359),
                Double.doubleToLongBits(calc.eval("pi=3.14159265359")));
        assertEquals(Double.doubleToLongBits(-1.0), Double.doubleToLongBits(calc.eval("cos(pi)")));

        assertEquals(Double.doubleToLongBits(33.0), Double.doubleToLongBits(calc.eval("2*2*3+4+3+2*7")));
        assertEquals(Double.doubleToLongBits(8.0), Double.doubleToLongBits(calc.eval("2*3+11-3*3")));
        assertEquals(Double.doubleToLongBits(30.2), Double.doubleToLongBits(calc.eval("((63/5)+3)*2-1")));

        assertEquals(Double.doubleToLongBits(33.0), Double.doubleToLongBits(calc.eval("a=33")));
        assertEquals(Double.doubleToLongBits(11.0), Double.doubleToLongBits(calc.eval("b=11")));
        assertEquals(Double.doubleToLongBits(5.0), Double.doubleToLongBits(calc.eval("c=5")));
        assertEquals(Double.doubleToLongBits(34.67),
                Double.doubleToLongBits(Math.round(calc.eval("a+(b*c)/a") * 100.0) / 100.0));
        assertEquals(Double.doubleToLongBits(39.67),
                Double.doubleToLongBits(Math.round(calc.eval("5+_") * 100.0) / 100.0));
        assertEquals(Double.doubleToLongBits(84.33),
                Double.doubleToLongBits(Math.round(calc.eval("_*2+(4+1)") * 100.0) / 100.0));

        assertEquals(Double.doubleToLongBits(-1.0), Double.doubleToLongBits(calc.eval("1-2*((3-(1+2)+1))")));
        assertEquals(Double.doubleToLongBits(0.84),
                Double.doubleToLongBits(Math.round(calc.eval("sin(cos(sqrt(2+6*(2+4))))") * 100.0) / 100.0));
        assertEquals(Double.doubleToLongBits(86.87),
                Double.doubleToLongBits(Math.round(calc.eval("cos(2.33)+sin(3.6)+(11*(2+6))") * 100.0) / 100.0));
    }
}