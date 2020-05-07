import io.emeraldpay.pjc.ss58.SS58Type;
import io.emeraldpay.pjc.types.*;

public class Examples {

    static byte[] getPubKey() {
        return Hash256.empty().getBytes();
    }

    static void address() {
        Address address = Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy");
        address = new Address(SS58Type.Network.LIVE, getPubKey());
    }

    static void dotAmount() {
        // create amount of 100,000 Plancks
        DotAmount amount = DotAmount.fromPlancks(100_000);

        // create 4.25 Dots, which is 4.25 * 10^12 == 4250000000000 Plancks
        amount = DotAmount.fromDots(4.25);

        // print formatted value
        // prints:
        // 4.25 Dot
        System.out.println(
                // automatic formatter find a best unit that gives the whole value, at this case it's Dots
                DotAmountFormatter.autoFormatter().format(amount)
        );

        // now let's divide it by 1000, which makes 4.25 Millidot
        // and then add 0.00112 Dot (or 1.12 Millidot)
        amount = amount
                .divide(1000)
                .add(DotAmount.fromDots(0.0012));

        // and print the result
        // prints:
        // 5.37 Millidot
        System.out.println(
                // auto formatter chooses Millidots as an optimal unit to display the value
                DotAmountFormatter.autoFormatter().format(amount)
        );
    }

    static void dotAmountFormat() {
        // Always print the whole value in plancks
        DotAmountFormatter formatter = DotAmountFormatter.fullFormatter();

        // Prints:
        // 12345600000 Planck
        System.out.println(
                formatter.format(DotAmount.fromDots(0.0123456))
        );

        // Finds optimal unit, and prints with 2 digits after decimal point
        formatter = DotAmountFormatter.autoFormatter();

        // Prints
        // 12.35 Millidot
        System.out.println(
                formatter.format(DotAmount.fromDots(0.0123456))
        );

        // Same as autoFormatter but gives a short name for the unit (Millidot becomes mDOT)
        formatter = DotAmountFormatter.autoShortFormatter();
        // Prints
        // 12.35 mDOT
        System.out.println(
                formatter.format(DotAmount.fromDots(0.0123456))
        );

        // Build a custom formatter
        formatter = DotAmountFormatter.newBuilder()
                // use 4 digits after decimal point, and comma (",") as a delimiter for groups
                .fullNumber("#,##0.0000")
                // display with Microdot unit
                .usingUnit(Units.Microdot)
                // then append "(of " string
                .exactString(" (of ")
                // display unit's short name
                .shortUnit()
                // and append string ")"
                .exactString(")")
                // finalize the formatter
                .build();

        // Prints
        // 12,345,600,000.0000 (of uDOT)
        System.out.println(
                formatter.format(DotAmount.fromDots(0.0123456))
        );
    }

    static void hash() {
        byte[] hashBytes = new byte[32];
        for (int i = 0; i < hashBytes.length; i++) {
            hashBytes[i] = (byte)i;
        }

        // create 256 bit hash from 32 byte array
        Hash256 hash256 = new Hash256(hashBytes);
        System.out.println(hash256);

        hashBytes = new byte[64];
        for (int i = 0; i < hashBytes.length; i++) {
            hashBytes[i] = (byte)i;
        }

        // create 512 bit hash from 64 byte array
        Hash512 hash512 = new Hash512(hashBytes);
        System.out.println(hash512);

        // format as a hex string
        String hash256String = hash256.toString();

        // create hash from hex string
        Hash256 hash256Copy = Hash256.from(hash256String);

        System.out.println(hash256Copy.equals(hash256));
    }

    public static void main(String[] args) {
        address();
        System.out.println("--------------------------------------------------");
        dotAmount();
        System.out.println("--------------------------------------------------");
        dotAmountFormat();
        System.out.println("--------------------------------------------------");
        hash();
    }
}
