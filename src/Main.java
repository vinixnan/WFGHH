
import experiments.Experiment_HH_WFG;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.util.JMException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vinicius
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //Experiment_MOGA_WFG.main(args);
            // TODO code application logic here
            //Experiment_IBEA_WFG.main(args);
           Experiment_HH_WFG.main(args);
           // Experiment_HH_Forced_WFG.main(args);
           // Experiment_NSGAII_WFG.main(args);
            //Experiment_SPEA2_WFG.main(args);
            //Experiment_MOGA_WFG.main(args);
           
            //Experiment_HH_Forced_WFG.main(args);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
