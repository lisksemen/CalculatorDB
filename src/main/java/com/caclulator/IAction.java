package com.caclulator;

public interface IAction {
    class Sin implements IAction {
        public double calculate(double number) {
            return Math.sin(number);
        }
    }

    class Sqrt implements IAction {

        public double calculate(double number) {
            return Math.sqrt(number);
        }
    }

    class Cos implements IAction {

        public double calculate(double number) {
            return Math.cos(number);
        }
    }

    class Atan implements IAction {
        public double calculate(double number) {
            return Math.atan(number);
        }

    }

    class Tan implements IAction {

        public double calculate(double number) {
            return Math.tan(number);
        }
    }

    class Log2 implements IAction {

        public double calculate(double number) {
            return Math.log(number) / Math.log(2);
        }
    }

    class Log10 implements IAction {

        public double calculate(double number) {
            return Math.log10(number);
        }
    }

    class UnaryMinus implements IAction {

        public double calculate(double number) {
            return -number;
        }
    }

    class UnaryPlus implements IAction {

        public double calculate(double number) {
            return number;
        }
    }


    double calculate(double number);
}
