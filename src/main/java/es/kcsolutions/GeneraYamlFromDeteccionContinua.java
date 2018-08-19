package es.kcsolutions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class GeneraYamlFromDeteccionContinua {
	
	public static class DatosMatricula {
		
		public String matricula;
		public int probabilidad;
		public String x1,y1;
		public String x2,y2;
		public String x3,y3;
		public String x4,y4;
		
	}

	/**
	 * Genera archivos yaml de imagenes(recortes) enviados al web service de carmen. Estos jpg se obtienen al modificar el anpr-daemon (aplicacion 
	 * escrita en C) para que copie el resultado de la invocacion a Carmen en la carpeta de salida asi como en otra donde poder recuperar las imagenes
	 */
	public static void main(String[] args) {
		
		String sourceFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/tmp2/";
		String targetFolderSinMatricula = sourceFolder + "sin_matricula/";
		
		int LONGITUD_MINIMA=0;
		int PROBABILIDAD_MINIMA=0;
		
		if (sourceFolder.endsWith("/") == false) sourceFolder += "/";
		if (targetFolderSinMatricula.endsWith("/") == false) targetFolderSinMatricula += "/";
		
		int totalYamlGenerados = 0;
		int totalYamlError = 0;
		
		File sinMatriculaFolder = new File(targetFolderSinMatricula);
		
		if (sinMatriculaFolder.exists() == false) {
			sinMatriculaFolder.mkdirs();
		}
		
		File[] sourceFolderList = new File(sourceFolder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.contains(".jpg");
			}
		});
		
		for(File f: sourceFolderList) {
			System.out.println("Procesando..." + f.getName());
			if (f.getName().indexOf("()") != -1) {
				f.renameTo(new File(targetFolderSinMatricula + f.getName()));
				continue;
			} 
			
			DatosMatricula datosMatricula = getDatos(f.getName());
			
			if (datosMatricula.probabilidad == 0) {
				System.out.println("La probabilidad es 0");
				//f.delete();
				continue;
			}
			
			if (datosMatricula.matricula.matches(".*\\d+.*") == false) {
				System.out.println("La matricula no tiene ningún número");
				//f.delete();
				continue;
			}
			
			if (datosMatricula.matricula.matches(".*[A-Za-z].*") == false) {
				System.out.println("La matricula no tiene ninguna letra");
				//f.delete();
				continue;
			}
			
			if (datosMatricula.probabilidad > PROBABILIDAD_MINIMA) {
				
				if (datosMatricula.matricula.indexOf("\\.") != -1) {
					System.out.println("Hay un punto en la matricula " + datosMatricula.matricula);
					//f.delete();
					continue;
				}
				
				if (datosMatricula.matricula.length() <= LONGITUD_MINIMA) {
					System.out.println("La matricula es muy corta");
					//f.delete();
					continue;
				}
				
				
				try {
					generaArchivoYaml(f, datosMatricula);
					totalYamlGenerados++;
				} catch(Exception exc) {
					System.out.println("Error al generar archivo yaml de " + f.getName());
					System.out.println("Detalles: " + exc.toString());
					totalYamlError++;
				}
			}
		}
		
		System.out.println("Total Yaml generados: " + totalYamlGenerados);
		System.out.println("Total Yaml error: " + totalYamlError);
		System.out.println("=== FIN EJECUCION ===");

	}
	
	private static void generaArchivoYaml(File ficheroJpg, DatosMatricula datosMatricula) throws IOException {
		
		int index = 0;
		FileOutputStream fout = new FileOutputStream(new File(ficheroJpg.getParent(), ficheroJpg.getName().replaceAll("\\.jpg", "-" + index + ".yaml")));

		fout.write(("image_file: " + ficheroJpg.getName()).getBytes());
		fout.write("\n".getBytes());

		BufferedImage bimg = ImageIO.read(ficheroJpg);
		// image_width: 1280
		fout.write(("image_width: " + bimg.getWidth()).getBytes());
		fout.write("\n".getBytes());
		// image_height: 720
		fout.write(("image_height: " + bimg.getHeight()).getBytes());
		fout.write("\n".getBytes());

		bimg = null;

		// plate_corners_gt: 398 617 489 622 490 637 399 631
		fout.write(("plate_corners_gt: " + datosMatricula.x1 + " " + datosMatricula.y1 + " " + 
				datosMatricula.x2 + " " + datosMatricula.y2 + " " + 
				datosMatricula.x3 + " " + datosMatricula.y3 + " " + 
				datosMatricula.x4 + " " + datosMatricula.y4).getBytes());
		
		fout.write("\n".getBytes());

		// plate_number_gt: 1
		fout.write(("plate_number_gt: " + datosMatricula.matricula).getBytes());
		fout.write("\n".getBytes());

		// plate_inverted_gt: false
		fout.write("plate_inverted_gt: false".getBytes());

		fout.close();
		
	}
	
	private static DatosMatricula getDatos(String nombre) {
		
		// 20180607152315836_3182981267955239471(9940JMT;66;274;116;371;112;372;131;275;135).jpg
		String datos = nombre.split("\\(")[1].substring(0, nombre.split("\\(")[1].length()-5);
		
		// ZONA6_foto-1000135_1295GRH(A295GRH;1;617;346;689;357;688;373;617;362)-0.jpg
		// ZONA6_foto-1054008_6699DFV(9345356G7;11;687;338;767;351;766;367;686;353)-30.jpg
		// ZONA6_foto-1031585_5943JKX(1U06270627D;2;214;27;365;46;364;87;213;68)-10.jpg
		//String datos = nombre.split("\\(")[1].substring(0, nombre.split("\\(")[1].length()-7);
		if(datos.endsWith(")")) {
			datos = datos.substring(0, datos.length()-1);
		}
		
		String tokens[] = datos.split(";");
		GeneraYamlFromDeteccionContinua.DatosMatricula datosMatricula = new GeneraYamlFromDeteccionContinua.DatosMatricula();
		datosMatricula.matricula = tokens[0];
		datosMatricula.probabilidad = new Integer(tokens[1]).intValue();
		
		datosMatricula.x1 = tokens[2];
		datosMatricula.y1 = tokens[3];
		
		datosMatricula.x2 = tokens[4];
		datosMatricula.y2 = tokens[5];
		
		datosMatricula.x3 = tokens[6];
		datosMatricula.y3 = tokens[7];
		
		datosMatricula.x4 = tokens[8];
		
		datosMatricula.y4 = "";
		for (int i = 0; i < tokens[9].length(); i++) {
	        if (Character.isDigit(tokens[9].charAt(i)) == true) {
	        	datosMatricula.y4 = datosMatricula.y4 + tokens[9].charAt(i);
	        }
	    } 
		
		return datosMatricula;
		
	}

}
