package beans;

import java.util.Arrays;
import java.util.List;

public enum Moeda {
    USD(true),
    EUR(true),
    GBP(true),
    CNY(false),
    BRL(false),
    ARS(false),
    CLP(false),
    MXN(false),
    CHF(true),
    AUD(true),
    JPY(true),
    KRW(false),
    CAD(true),
    INR(false),
    SGD(false),
    AED(false);

    private final boolean temPtax;

    Moeda(boolean temPtax) {
        this.temPtax = temPtax;
    }

    public boolean temPtax() {
        return temPtax;
    }

    public boolean isReal() {
        return this == BRL;
    }

    /** A moeda pode ser usada por um fornecedor: é o Real (sem conversão) ou tem cotação PTAX. */
    public boolean suportada() {
        return isReal() || temPtax;
    }

    public static List<Moeda> suportadas() {
        return Arrays.stream(values()).filter(Moeda::suportada).toList();
    }
}