package org.zza.codegenerator.threeaddresscode;

import org.zza.codegenerator.DataMemoryManager;


public class Assignment3AC extends ThreeAddressCode {
    
    public Assignment3AC(int lineNumber, DataMemoryManager manager) {
        super(lineNumber, manager);
    }
    
    @Override
    public void emitCode() {
        if ("0123456789".contains(Character.toString(firstParam.charAt(0)))) {
            System.out.println(lineNumber++ + ":    LDC 0," + firstParam  + "(6)");
        } else {
            System.out.println(lineNumber++ + ":    LD  0," + firstParam  + "(6)");//Register 6 holds a 0;
        }
        System.out.println(lineNumber + ":    ST  0," + secondParam + "(6)");
    }
    
    @Override
    public void emitComments() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int getEmittedSize() {
        return 2;
    }
    
}
