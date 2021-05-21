import apps.ReceiverApp;
import apps.SenderApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.ArrayDeque;
import de.ipvs.rni.exercise2.common.*;
import de.ipvs.rni.exercise2.layers.*;
import de.ipvs.rni.exercise2.packets.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class FullStackTest {
    // This should avoid endless loops; be cautious!
	@Rule
    public Timeout globalTimeout = Timeout.seconds(10);
    
    @Test
    public void test11(){
         String text = "Freilebende Gummibärchen gibt es nicht. Man kauft sie in Packungen an der Kinokasse. Dieser Kauf ist der Beginn einer fast erotischen und sehr ambivalenten Beziehung Gummibärchen-Mensch. Zuerst genießt man. Dieser Genuß umfaßt alle Sinne. Man wühlt in den Gummibärchen, man fühlt sie. Gummibärchen haben eine Konsistenz wie weichgekochter Radiergummi. Die Tastempfindung geht auch ins Sexuelle. Das bedeutet nicht unbedingt, daß das Verhältnis zum Gummibärchen ein geschlechtliches wäre, denn prinzipiell sind diese geschlechtsneutral. Nun sind Gummibärchen weder wabbelig noch zäh; sie stehen genau an der Grenze. Auch das macht sie spannend. Gummibärchen sind auf eine aufreizende Art weich. Und da sie weich sind, kann man sie auch ziehen. Ich mache das sehr gerne. Ich sitze im dunklen Kino und ziehe meine Gummibärchen in die Länge, ganz ganz langsam. Man will sie nicht kaputtmachen, und dann siegt doch die Neugier, wieviel Zug so ein Bärchen aushält. (Vorstellbar sind u.a. Gummibärchen-Expander für Kinder und Genesende). Forscherdrang und gleichzeitig das Böse im Menschen erreichen den Climax, wenn sich die Mitte des gezerrten Bärchens von Millionen Mikrorissen weiß färbt und gleich darauf das zweigeteilte Stück auf die Finger zurückschnappt. Man hat ein Gefühl der Macht über das hilflose, nette Gummibärchen. Und wie man damit umgeht: Mensch erkenne dich selbst!  "
                    + "Jetzt ist es so, daß Gummibärchen ja nicht gleich Gummibärchen ist. Ich bevorzuge das klassische Gummibärchen, künstlich gefärbt und aromatisiert. Mag sein, daß es eine Sentimentalität ist. Jedenfalls halte ich nichts von neuartigen Alternativ-Gummibärchen ohne Farbstoff (»Mütter, mit viel Vitamin C«), und auch unter den konventionellen tummeln sich schwarze Schafe: die schwarzen Lakritz-Bärchen. Wenn ich mit Xao im Kino bin, red ich ihm so lange ein, daß das die besten sind, bis er sie alle ißt. Sie schmecken scheußlich und fühlen sich scheußlich an. Dagegen das schöne, herkömmliche Gummibärchen: allein wie es neonhaft vom Leinwandleuchten illuminiert, aber ganz ohne die Kühle der Reklameröhren!"
                    + "Die nächste prickelnde Unternehmung ist das Kauen des Gummibärchens. Es ist ein Katz-und-Maus-Spiel. Man könnte zubeißen, läßt aber die Spannung noch steigen. Man quetscht das nasse Gummibärchen zwischen Zunge und Gaumen und glibscht es durch den Mund. Nach einer Zeit beiße ich zu, oft bei nervigen Filmszenen. Es ist eine animalische Lust dabei. Was das schmecken angeht. wirken Gummibärchen in ihrer massiven Fruchtigkeit sehr dominierend. Zigaretten auf Gummibärchen schmecken nicht gut."
                    + "Anführen sollte man auch noch: manche mögen die Grünen am liebsten, manche die Gelben. Ich mag am liebsten die Roten. Sie glühen richtig rot, und ihr Himbeergeschmack fährt wie Napalm über die Geschmacksknospen."
                    + "Eine meiner Lieblingsphantasien, wo es um Gummibärchen geht, ist der Gummibär. Ich will einen riesigen Gummibären. Jeder wahre Gummibärchen-Gourmet wird mich verstehen. Ebenfall phantasieanregend können sie eingesetzt werden zum Aufbau verschiedener »Orgiengruppen- Modelle« oder als »Demonstrationsobjekt für wirbellose Tiere«."
                    + "Abgesehen vom diabolischen Lustgewinn müßte man die Bärchen gar nicht zerreißen. Sie sind ja durchscheinend."
                    + "Zu behaupten, daß sich im Gummibärchen das Wesen aller Dinge offenbart, finde ich keinesfalls als gewagt. Wer schon einmal über einem roten Gummibärchen meditiert hat, weiß von diesen Einsichten."
                    + "Wenn ich das Kino verlasse oder die Packung einfach leergegessen ist, habe ich meist ein Gefühl, als hätte mir einer in den Magen getreten. Hier schläft die gesteigerte Intensität - als deren Ursache den Gummibärchen durchaus der Charakter einer Droge zuerkannt werden kann - ins Negative um, in den Überdruß. In dichter und geraffter Form spiegelt sich im Verhältnis zum Gummibärchen eine menschliche Love-Affair wider. Nie wieder Gummibärchen, denke ich jedesmal. In der Zwischenzeit lächle ich dann über den Absolutheitsanspruch den diese Momente erheben."

                    + "Schon zu Hause beunruhigen mich wieder Gerüchte über einen Marktvorstoß der Japaner mit Gummireis oder Gummischweinen. Und wieder und wieder geht es mir durch den Kopf: Gummibärchen sind Spitze. "
                    + "Freilebende Gummibärchen gibt es nicht. Man kauft sie in Packungen an der Kinokasse. Dieser Kauf ist der Beginn einer fast erotischen und sehr ambivalenten Beziehung Gummibärchen-Mensch. Zuerst genießt man. Dieser Genuß umfaßt alle Sinne. Man wühlt in den Gummibärchen, man fühlt sie. Gummibärchen haben eine Konsistenz wie weichgekochter Radiergummi. Die Tastempfindung geht auch ins Sexuelle. Das bedeutet nicht unbedingt, daß das Verhältnis zum Gummibärchen ein geschlechtliches wäre, denn prinzipiell sind diese geschlechtsneutral. Nun sind Gummibärchen weder wabbelig noch zäh; sie stehen genau an der Grenze. Auch das macht sie spannend. Gummibärchen sind auf eine aufreizende Art weich. Und da sie weich sind, kann man sie auch ziehen. Ich mache das sehr gerne. Ich sitze im dunklen Kino und ziehe meine Gummibärchen in die Länge, ganz ganz langsam. Man will sie nicht kaputtmachen, und dann siegt doch die Neugier, wieviel Zug so ein Bärchen aushält. (Vorstellbar sind u.a. Gummibärchen-Expander für Kinder und Genesende). Forscherdrang und gleichzeitig das Böse im Menschen erreichen den Climax, wenn sich die Mitte des gezerrten Bärchens von Millionen Mikrorissen weiß färbt und gleich darauf das zweigeteilte Stück auf die Finger zurückschnappt. Man hat ein Gefühl der Macht über das hilflose, nette Gummibärchen. Und wie man damit umgeht: Mensch erkenne dich selbst!  "
                    + "Jetzt ist es so, daß Gummibärchen ja nicht gleich Gummibärchen ist. Ich bevorzuge das klassische Gummibärchen, künstlich gefärbt und aromatisiert. Mag sein, daß es eine Sentimentalität ist. Jedenfalls halte ich nichts von neuartigen Alternativ-Gummibärchen ohne Farbstoff (»Mütter, mit viel Vitamin C«), und auch unter den konventionellen tummeln sich schwarze Schafe: die schwarzen Lakritz-Bärchen. Wenn ich mit Xao im Kino bin, red ich ihm so lange ein, daß das die besten sind, bis er sie alle ißt. Sie schmecken scheußlich und fühlen sich scheußlich an. Dagegen das schöne, herkömmliche Gummibärchen: allein wie es neonhaft vom Leinwandleuchten illuminiert, aber ganz ohne die Kühle der Reklameröhren!"
                    + "Die nächste prickelnde Unternehmung ist das Kauen des Gummibärchens. Es ist ein Katz-und-Maus-Spiel. Man könnte zubeißen, läßt aber die Spannung noch steigen. Man quetscht das nasse Gummibärchen zwischen Zunge und Gaumen und glibscht es durch den Mund. Nach einer Zeit beiße ich zu, oft bei nervigen Filmszenen. Es ist eine animalische Lust dabei. Was das schmecken angeht. wirken Gummibärchen in ihrer massiven Fruchtigkeit sehr dominierend. Zigaretten auf Gummibärchen schmecken nicht gut."
                    + "Anführen sollte man auch noch: manche mögen die Grünen am liebsten, manche die Gelben. Ich mag am liebsten die Roten. Sie glühen richtig rot, und ihr Himbeergeschmack fährt wie Napalm über die Geschmacksknospen."
                    + "Eine meiner Lieblingsphantasien, wo es um Gummibärchen geht, ist der Gummibär. Ich will einen riesigen Gummibären. Jeder wahre Gummibärchen-Gourmet wird mich verstehen. Ebenfall phantasieanregend können sie eingesetzt werden zum Aufbau verschiedener »Orgiengruppen- Modelle« oder als »Demonstrationsobjekt für wirbellose Tiere«."
                    + "Abgesehen vom diabolischen Lustgewinn müßte man die Bärchen gar nicht zerreißen. Sie sind ja durchscheinend."
                    + "Zu behaupten, daß sich im Gummibärchen das Wesen aller Dinge offenbart, finde ich keinesfalls als gewagt. Wer schon einmal über einem roten Gummibärchen meditiert hat, weiß von diesen Einsichten."
                    + "Wenn ich das Kino verlasse oder die Packung einfach leergegessen ist, habe ich meist ein Gefühl, als hätte mir einer in den Magen getreten. Hier schläft die gesteigerte Intensität - als deren Ursache den Gummibärchen durchaus der Charakter einer Droge zuerkannt werden kann - ins Negative um, in den Überdruß. In dichter und geraffter Form spiegelt sich im Verhältnis zum Gummibärchen eine menschliche Love-Affair wider. Nie wieder Gummibärchen, denke ich jedesmal. In der Zwischenzeit lächle ich dann über den Absolutheitsanspruch den diese Momente erheben.";
        
        GlobalClock clock = new GlobalClock();
        SenderApp sendApp = new SenderApp(text);
        ReceiverApp recvApp = new ReceiverApp();

        ApplicationLayer appLayerA = new ApplicationLayer();
        DataLinkLayer dataLinkLayerA = new DataLinkLayer();
        
        ApplicationLayer appLayerB = new ApplicationLayer();
        DataLinkLayer dataLinkLayerB = new DataLinkLayer();
        
        PhysicalLayer physicalLayer = new PhysicalLayer();
        
        appLayerA.bind(sendApp);
        dataLinkLayerA.bind(appLayerA);

        appLayerB.bind(recvApp);
        dataLinkLayerB.bind(appLayerB);
        
        physicalLayer.connectA(dataLinkLayerA);
        physicalLayer.connectB(dataLinkLayerB);
        
        clock.registerCallback(sendApp);
        clock.registerCallback(recvApp);
        clock.registerCallback(appLayerA);
        clock.registerCallback(appLayerB);
        clock.registerCallback(dataLinkLayerA);
        clock.registerCallback(dataLinkLayerB);
        clock.registerCallback(physicalLayer);
        
         try {
            while(recvApp.getMsg() == null){
                clock.tempusFugit();
            }
        } catch (InterruptedException ex) {
             Logger.getLogger(ReceiverApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(sendApp.getMsg(), recvApp.getMsg());
    }
}
