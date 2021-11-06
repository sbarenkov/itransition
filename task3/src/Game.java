import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class Game {
    public static ArrayList<String> getComputerTurn(ArrayList<String> list) throws NoSuchAlgorithmException, InvalidKeyException{
        ArrayList<String> computerTurn = new ArrayList<>(3);
        String moveName;
        int moveNumber=(int)(Math.random()*list.size());
        moveName=list.get(moveNumber);
        String hmacKey = getHMAC(moveName);
        computerTurn.add(moveName);
        computerTurn.add(Integer.toString(moveNumber));
        computerTurn.add(hmacKey);
        return computerTurn;
    }

    public static String getHMAC(String moveName) throws NoSuchAlgorithmException, InvalidKeyException{
        Mac mac = Mac.getInstance("HmacSHA256");
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String hmacKey = encoder.encodeToString(bytes);
        mac.init(new SecretKeySpec(hmacKey.getBytes(),"HmacSHA256"));
        byte[] hmac = mac.doFinal(moveName.getBytes());
        System.out.printf("HMAC: %032x%n", new BigInteger(1, hmac));
        return hmacKey;
    }

    public static void availableMoves(ArrayList<String> moves){
        System.out.println("Available moves:");
        for (int i = 0; i < moves.size(); i++) {
            System.out.println(i+1+" - "+moves.get(i));
        }
        System.out.print("0 - exit\nEnter your move: ");
    }

    public static Integer getPlayerTurn(ArrayList<String> moves){
        int moveNumber;
        Scanner in = new Scanner(System.in);
        while(true){
            availableMoves(moves);
            if (in.hasNextInt()){
                moveNumber=in.nextInt()-1;
                if (moveNumber>=0&&moveNumber<moves.size()){
                    break;
                }
                else if (moveNumber==-1){
                    System.exit(0);
                }
            }
            else {
                in.next();
            }
        }
        System.out.println("Your move: "+ moves.get(moveNumber));
        return moveNumber;
    }

    public static void getResult(ArrayList<String> moves, ArrayList<String> computerTurn, int playerTurn){
        System.out.println("Computer move: "+computerTurn.get(0));
        int difference = Integer.parseInt(computerTurn.get(1)) - playerTurn;
        if (difference==0){
            System.out.println("Draw!");
        }
        else if (difference>0){
            if (difference<= (moves.size()/2)){
                System.out.println("Computer win!");
            }
            else {
                System.out.println("You win!");
            }
        }
        else {
            if (-difference<= (moves.size()/2)){
                System.out.println("You win!");
            }
            else {
                System.out.println("Computer win!");
            }
        }
        System.out.println("HMAC key:" + computerTurn.get(2));
    }

    public static void menu(ArrayList<String> moves) throws NoSuchAlgorithmException, InvalidKeyException{
        ArrayList<String> computerTurn= getComputerTurn(moves);
        getResult(moves, computerTurn, getPlayerTurn(moves));
    }

    public static boolean checkList(ArrayList<String> moves){
        boolean ch1=false, ch2=false, ch3=false;
        if (moves.size()%2==0){
            System.out.println("Even number of moves");
            ch1=true;
        }
        if (moves.size()<3) {
            System.out.println("Small number of moves");
            ch2=true;
        }
        if (moves.size()!=(new ArrayList<>(new LinkedHashSet<>(moves))).size()){
            System.out.println("Repetitive moves");
            ch3=true;
        }
        if (ch1||ch2||ch3){
            System.out.println("Correct example:\n>java -jar .../itransition.jar rock paper scissors lizard Spock");
            return false;
        }
        else {
            return true;
        }
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException{
        ArrayList<String> moves = new ArrayList<>(args.length);
        Collections.addAll(moves, args);
        if (checkList(moves)){
            menu(moves);
        }
    }
}
