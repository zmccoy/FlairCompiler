package org.zza.codegenerator.threeaddresscode;

import org.zza.codegenerator.DataMemoryManager;


public class Addition3AC extends ThreeAddressCode {

    public Addition3AC(int lineNumber, DataMemoryManager manager) {
        super(lineNumber, manager);
    }

    @Override
    public void emitCode() {
        if (isDigit(secondParam.charAt(0))) {
            System.out.println(lineNumber++ + ":   LDC  0," + secondParam + ZERO_REGISTER);//Register 6 holds a 0;
        } else {
            System.out.println(lineNumber++ + ":    LD  0," + manager.getAddressOfVar(secondParam) + OFFSET_REGISTER);//Register 6 holds a 0;
        }

        if (isDigit(thirdParam.charAt(0))) {
            System.out.println(lineNumber++ + ":   LDC  1," + thirdParam + ZERO_REGISTER); 
        } else {
            System.out.println(lineNumber++ + ":    LD  1," + manager.getAddressOfVar(thirdParam) + OFFSET_REGISTER);
        }
        System.out.println(lineNumber++ + ":   ADD  0,0,1");//Register 5 is Return Value
        System.out.println(lineNumber + ":    ST  0," + manager.getAddressOfVar(firstParam) + OFFSET_REGISTER);
    }

    @Override
    public void emitComments() {
        
    }

    @Override
    public int getEmittedSize() {
        return 4;
    }
    
}
