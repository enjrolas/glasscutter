import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.pdf.*; 
import controlP5.*; 
import javax.swing.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class glassCutter_1_5 extends PApplet {



 

PFont font;

ControlP5 cp5;
CheckBox checkbox;
Slicer slicer;
int slice, thickSlice;
int microslices;
String mode="settings";
boolean exportPDF=false, exportPNG=false;

int currentSlice=0;
boolean verbose=true;


public void setup()
{ 
  size(500, 500);
  ImageIcon titlebaricon = new ImageIcon(loadBytes("glass cutter.gif"));
  frame.setIconImage(titlebaricon.getImage());  
  setupGui();
}

public void draw()
{
  //if we're done slicing
  if (mode.equals("slices"))
  {
    font=loadFont("AmericanTypewriter-15.vlw");
    textFont(font);
    background(255);
    cp5.draw();
    int slice=0;
    for (slice=thickSlice*microslices;(slice<(thickSlice+1)*microslices)&&(slice<slicer.slices.size());slice++)
    {
      for (int i=0;i<((Slice)slicer.slices.get(slice)).segments.size();i++)
      {
        Line segment=((Line)((Slice)slicer.slices.get(slice)).segments.get(i));
        segment.drawSegment(0);
      }        
      slice++;
    }
    String sliceString="slice "+thickSlice+" / "+slicer.slices.size()/slicer.microslices;
    fill(0);
    text(sliceString, width-textWidth(sliceString)-20, 20);
  }

  //if we're in the process of slicing
  else if (mode.equals("slicing"))
  {
    background(0);
    cp5.draw();
    font=loadFont("AmericanTypewriter-20.vlw");
    textFont(font);
    fill(255);
    if(slicer.status!=null)
      text(slicer.status, 100, 100);
    if (slicer.status.equals("slicing"))
      progressBar(slicer.slices.size(), ceil(slicer.microslices*slicer.getAxis(slicer.bound, slicer.axis)/slicer.thickness), 100, 130, 200, 20, slicer.startTime);
    if (slicer.status.equals("exporting PDF"))
      progressBar(slicer.page, ceil(slicer.slices.size()/(slicer.rows*slicer.columns))/slicer.microslices, 100, 130, 200, 20, slicer.startTime);
    if (slicer.status.equals("exporting PNG"))
      progressBar(thickSlice, ceil(slicer.slices.size()/slicer.microslices), 100, 130, 200, 20, slicer.startTime);
    if (slicer.status.equals("finished"))
      mode="slices";
  }
  else
  {
    background(0);
    cp5.draw();
  }
}

public void progressBar(int current, int max, int x, int y, int barWidth, int barHeight, int startTime)
{
  stroke(0, 0, 255);
  fill(0, 0, 255);
  rect(x, y, barWidth*current/max, barHeight);
  stroke(255);
  noFill();
  rect(x, y, barWidth, barHeight);
  int timeSoFar=millis()-startTime;
  int totalTime;
  if (current!=0)
    totalTime=timeSoFar*max/current;
  else 
    totalTime=0;
  int timeRemaining=totalTime-timeSoFar;
  int minutesRemaining=timeRemaining/1000/60;
  int secondsRemaining=timeRemaining/1000%60;
  String formattedTimeRemaining="estimated time remaining:   "+nf(minutesRemaining, 2)+":"+nf(secondsRemaining, 2);
  fill(255);
  if (current!=0)
    text(formattedTimeRemaining, x, y+barHeight+20);
}


public void keyPressed()
{
  if (slicer!=null)
  {
    if ((keyCode==UP)||(keyCode==RIGHT))
      thickSlice++;
    ;  
    if ((keyCode==DOWN)||(keyCode==LEFT))
      thickSlice--;
    if (thickSlice<0)
      thickSlice=slicer.slices.size()/slicer.microslices-1;
    if (thickSlice>=slicer.slices.size()/slicer.microslices)
      thickSlice=0;  
    if (verbose)
      println("current slice:  "+thickSlice+"/"+slicer.slices.size()+" slices");
  }
}



public void setupGui()
{
  font=loadFont("AmericanTypewriter-15.vlw");
  cp5 = new ControlP5(this);
  cp5.setAutoDraw(false);  

  cp5.addTextfield("filePath")
    .setPosition(20, 30)
      .setSize(350, 20)
        .setFont(loadFont("AmericanTypewriter-10.vlw"))
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setLabelVisible(false)
                //.setValue("/Users/apple/Dropbox/Manila Mantis/3D scans/mikan-sample/mikan rotated.ply")
                //                .setValue("/Users/alex/Documents/LookingGlass/slices/jane with bird - aug 3 2013.ply")
                //.setValue("/Users/alex/Documents/LookingGlass/slices/shawn thinking - aug 3 2013.ply")
                //.setValue("C:\\Users\\asus-hadd\\Downloads\\mikan-rotated.ply");
                ;

  cp5.addBang("chooseFile")
    .setPosition(400, 30)
      .setSize(60, 20)
        .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
          ;



  cp5.addTextfield("x")
    .setPosition(20, 100)
      .setSize(40, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("58.3")
                ;

  cp5.addTextfield("y")
    .setPosition(70, 100)
      .setSize(40, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("98.3")
                ;

  cp5.addTextfield("z")
    .setPosition(120, 100)
      .setSize(40, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("58.3")
                ;


  cp5.addTextfield("pageWidth")
    .setPosition(300, 100)
      .setSize(50, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("420")
                ;

  cp5.addTextfield("pageHeight")
    .setPosition(380, 100)
      .setSize(50, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("450")
                ;


  cp5.addTextfield("rows")
    .setPosition(380, 140)
      .setSize(50, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("7")
                ;

  cp5.addTextfield("columns")
    .setPosition(300, 140)
      .setSize(50, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("4")
                ;

  cp5.addTextlabel("spacingLabel")
    .setText("spacing between slices on sheet, in mm")
      .setPosition(300, 180)
        .setColorValue(0xFFFFFF00)
          ;

  cp5.addTextfield("spacing")
    .setPosition(380, 200)
      .setSize(50, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("0")
                ;

  cp5.addTextlabel("marginLabel")
    .setText("Margin to the edge of the page")
      .setPosition(300, 240)
        .setColorValue(0xFFFFFF00)
          ;

  cp5.addTextfield("pageMargin")
    .setPosition(380, 260)
      .setSize(50, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("3")
                ;


  cp5.addRadioButton("sliceDirection")
    .setPosition(20, 200)
      .setSize(40, 20)
        .setColorForeground(color(120))
          .setColorActive(color(255))
            .setColorLabel(color(255))
              .setItemsPerRow(3)
                .setSpacingColumn(50)
                  .addItem("x_axis", 1)
                    .addItem("y_axis", 2)
                      .addItem("z_axis", 3)
                        .activate("z_axis")
                          ;


  cp5.addTextfield("sliceThickness")
    .setPosition(20, 300)
      .setSize(30, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("0.3")
                .setLabelVisible(false)
                  ;

  cp5.addTextfield("microslices")
    .setPosition(200, 300)
      .setSize(30, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("1")
                .setLabelVisible(false)
                  ;


  cp5.addTextlabel("dimensions")
    .setText("Box Dimensions in mm")
      .setPosition(20, 80)
        .setColorValue(0xFFFFFF00)
          ;

  cp5.addTextlabel("pageDimensions")
    .setText("output page dimensions in mm")
      .setPosition(300, 80)
        .setColorValue(0xFFFFFF00)
          ;


  cp5.addTextlabel("sliceAxis")
    .setText("Slicing Axis")
      .setPosition(20, 185)
        .setColorValue(0xFFFFFF00)
          ;

  cp5.addTextlabel("sliceThicknessLabel")
    .setText("Slice Thickness in mm")
      .setPosition(20, 285)
        .setColorValue(0xFFFFFF00)
          ;


  cp5.addButton("slice")
    .setPosition(200, 350)
      .setSize(80, 80)
        .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
          ;

  cp5.addTab("slicedModel")
    .setColorBackground(color(0, 160, 100))
      .setColorLabel(color(255))
        .setColorActive(color(255, 128, 0))
          .setLabel("Sliced Model")
            .setId(2)
              .activateEvent(true)
                ;

  checkbox=cp5.addCheckBox("export options")
    .setPosition(300, 300)
      .setColorForeground(color(120))
        .setColorActive(color(255))
          .setColorLabel(color(255))
            .setSize(10, 10)
              .setItemsPerRow(2)
                .setSpacingColumn(40)
                  .addItem("PDF", 0)
                    .addItem("PNG", 1)
                        ;

  cp5.addTextfield("resolution")
    .setPosition(380, 340)
      .setSize(30, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("600")
                .setLabelVisible(false)
                  ;

  cp5.addTextfield("overlap")
    .setPosition(380, 400)
      .setSize(30, 20)
        .setFont(font)
          .setFocus(true)
            .setColor(color(255, 0, 0))
              .setValue("0")
                .setLabelVisible(false)
                  ;

  cp5.getTab("default")
    .activateEvent(true)
      .setLabel("Slice Settings")
        .setId(1)
          ;
}

public void controlEvent(ControlEvent theEvent) {
  String event=theEvent.getName();
  println(event);

  if (theEvent.isFrom(checkbox)) {
    // checkbox uses arrayValue to store the state of 
    // individual checkbox-items. usage:
    exportPDF=(checkbox.getArrayValue()[0]==1);
    exportPNG=(checkbox.getArrayValue()[1]==1);
  }
  if (event.equals("chooseFile"))
    cp5.get(Textfield.class, "filePath").setText(fileChooser());
  if (theEvent.isTab()) {
    println(theEvent.getTab().getName());
    if (theEvent.getTab().getName().equals("slicedModel"))
      if (slicer!=null)
        mode="slices";
    if (theEvent.getTab().getName().equals("default"))
    {
      mode="settings";
      cp5.getController("slice").setLock(false);
    }
  }
  else if (event.equals("slice"))
  {
    cp5.getTab("slicedModel").bringToFront();
    cp5.getController("slice").setLock(true);    
    mode="slicing";
    thread("sliceModel");
  }
}

public void sliceModel()
{
  float x, y, z, thickness;
  String filename;
  char axis=' ';
  int sliceDirection=(int)cp5.get("sliceDirection").getValue();
  switch(sliceDirection) {
    case(1):
    axis='x';
    break;
    case(2):
    axis='y';
    break;
    case(3):
    axis='z';
    break;
  }
  x=PApplet.parseFloat(cp5.get(Textfield.class, "x").getText());
  y=PApplet.parseFloat(cp5.get(Textfield.class, "y").getText());
  z=PApplet.parseFloat(cp5.get(Textfield.class, "z").getText());
  thickness=PApplet.parseFloat(cp5.get(Textfield.class, "sliceThickness").getText());
  microslices=PApplet.parseInt(cp5.get(Textfield.class, "microslices").getText());
  filename=cp5.get(Textfield.class, "filePath").getText();
  if (verbose)
  {
    println("filename: "+filename);
    println("bounding box dimensions: "+x+" x "+y+" x "+z);
    println("slicing axis: "+axis);
    println("slice thickness: "+thickness);
  }

  float pageWidth=PApplet.parseFloat(cp5.get(Textfield.class, "pageWidth").getText());
  float pageHeight=PApplet.parseFloat(cp5.get(Textfield.class, "pageHeight").getText());
  float spacing=PApplet.parseFloat(cp5.get(Textfield.class, "spacing").getText());
  float pageMargin=PApplet.parseFloat(cp5.get(Textfield.class, "pageMargin").getText());
  int rows=PApplet.parseInt(cp5.get(Textfield.class, "rows").getText());
  int columns=PApplet.parseInt(cp5.get(Textfield.class, "columns").getText());
  int resolution=PApplet.parseInt(cp5.get(Textfield.class, "resolution").getText());
  float overlap=PApplet.parseFloat(cp5.get(Textfield.class, "overlap").getText());
  slicer=new Slicer(filename, x, y, z, thickness, axis, microslices, overlap);
  slicer.process();
  if (exportPDF)
    slicer.savePDF(pageWidth, pageHeight, rows, columns, pageMargin, spacing);
  if (exportPNG)
    slicer.savePNG(resolution);
}


public String fileChooser()
{
  // set system look and feel 

  try { 
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  } 
  catch (Exception e) { 
    e.printStackTrace();
  } 

  // create a file chooser 
  final JFileChooser fc = new JFileChooser(); 

  // in response to a button click: 
  int returnVal = fc.showOpenDialog(this); 

  if (returnVal == JFileChooser.APPROVE_OPTION) { 
    File file = fc.getSelectedFile(); 
    // see if it's an image 
    // (better to write a function and check for all supported extensions) 
    if (file.getName().endsWith("ply")) { 
      return file.getPath();
    } 
    else { 
      return "";
    }
  }
  else
    return "";
}




class Slicer {
  ArrayList vertices;
  ArrayList triangles;
  ArrayList slices;

  PVector displayCenter;
  float displayScalingFactor;
  Vertex min, max;
  PVector bound;
  char axis='x';
  int startTime;
  PFont font;
  float margins=.05f;  //5% margins
  float scale=0.352f;  //1 pixel in a pdf, scaled to 100%, will print as 0.352 mm
  int totalSlices;
  int weight=1;
  int slice=0;
  int currentSlice=0;
  int rows, columns;
  int page;
  String filename="";
  String directory="";
  String file="";
  int microslices=1;
  float thickness=0.3f;
  String status="idle";
  boolean error=false;
  float spacing, pageMargin;
  float overlap=0;  //number of overlapping microslices  

  Slicer(String _filename, float boundX, float boundY, float boundZ, float _thickness, char _axis, int _microslices, float _overlap)
  { 
    axis=_axis;
    thickness=_thickness;
    microslices=_microslices;
    filename=_filename;
    overlap=_overlap;
    vertices=new ArrayList<Vertex>();
    triangles=new ArrayList<Triangle>();
    slices=new ArrayList<Slice>();
    bound=new PVector(boundX, boundY, boundZ);
    String[] parts=filename.split(File.pathSeparator);
    if (verbose)
      println(parts);
    directory="";
    for (int i=0;i<parts.length-1;i++)
      directory+=parts[i]+File.pathSeparator;
    if (verbose)
      println(directory);
    file=parts[parts.length-1];
    String[] fileParts=split(file, ".");
    file=fileParts[0];
    if (verbose)
      println(file);
  }
  public void process()
  {
    loadPLY(filename);
    scaleModel();

    float[] displayScalingFactors=new float[2];

    switch(axis) {    
      case('x'):
      displayScalingFactors[0]=width/bound.y;
      displayScalingFactors[1]=height/bound.z;
      break;
      case('y'):
      displayScalingFactors[0]=width/bound.x;
      displayScalingFactors[1]=height/bound.z;
      break;
      case('z'):
      displayScalingFactors[0]=width/bound.x;
      displayScalingFactors[1]=height/bound.y;
      break;
    }
    displayScalingFactor=sort(displayScalingFactors)[0];
    if (verbose)
    {
      println("display scaling factor between the scaled model and the "+width+" X "+height+" display is: "+displayScalingFactor);
      println("display center is:" + displayCenter);
      print("min is: ");
      min.printVertex();
      print("max is: ");
      max.printVertex();
    }
    sliceModel(thickness, axis);
  }

  //export slices to PDF
  public void savePDF(float pageWidth, float pageHeight, int _rows, int _columns, float _pageMargin, float _spacing)
  {
    status="exporting PDF";
    startTime=millis();
    rows=_rows;
    columns=_columns;
    spacing=_spacing;
    pageMargin=_pageMargin;
    PGraphics pdf;
    float scale=0.352f;  //1 pixel in a pdf, scaled to 100%, will print as 0.352 mm
    pdf=createGraphics((int)(pageWidth/scale), (int)(pageHeight/scale), PDF, directory+file+"-"+axis+"-axis-"+microslices+"-microslices"+".pdf");
    textMode(SHAPE);
    PFont font=createFont("Constantia", 15);
    //    println(PGraphicsPDF.listFonts());
    //    pdf.textFont(font);
    //    scale*=displayScalingFactor;
    if (verbose)
    {
      println(displayScalingFactor);
      println(scale);
    }
    pdf.beginDraw();
    pdf.background(255);
    page=0;
    PVector sliceBoundary=new PVector(0, 0);

    //figure out the bounding dimensions of a slice
    switch(axis)
    {
      case('x'):
      sliceBoundary=new PVector(bound.y, bound.z);
      break;
      case('y'):
      sliceBoundary=new PVector(bound.x, bound.z);
      break;
      case('z'):
      sliceBoundary=new PVector(bound.x, bound.y);
      break;
    }
    for (thickSlice=0;thickSlice<slices.size()/microslices;thickSlice++)
    {
      if (verbose)
        println("drawing slice " + thickSlice +" / "+ slices.size()/microslices);
      pdf.pushMatrix();
      pdf.translate((pageMargin + (spacing + sliceBoundary.x)*(thickSlice%columns))/scale, (pageMargin + (spacing + sliceBoundary.y)*(thickSlice/columns%rows))/scale);
      drawCrosshatches(pdf, 0, 0, (int)(sliceBoundary.x/scale), (int)(sliceBoundary.y/scale), (int)(5/scale), thickSlice);
      for (slice=thickSlice*microslices;(slice<(((thickSlice+1)*microslices)+(int)(overlap*microslices)))&&(slice<slicer.slices.size());slice++)
      {
        for (int i=0;i<((Slice)slices.get(slice)).segments.size();i++)
        {
          Line segment=((Line)((Slice)slices.get(slice)).segments.get(i));
          pdf.beginShape();
          pdf.strokeWeight(0.5f);
          pdf.stroke(segment.start.vertexColor);
          pdf.vertex(segment.start.point.x/displayScalingFactor/scale, segment.start.point.y/displayScalingFactor/scale);           
          pdf.stroke(segment.end.vertexColor);
          pdf.vertex(segment.end.point.x/displayScalingFactor/scale, segment.end.point.y/displayScalingFactor/scale);          
          pdf.endShape();
        }
      }
      pdf.popMatrix();
      if (thickSlice%(rows*columns)==(rows*columns)-1)
      {
        PGraphicsPDF doc = (PGraphicsPDF) pdf;  // Get the renderer
        doc.nextPage();  // Tell it to go to the next page
        page++;
        pdf.background(255);
      }
    }

    pdf.dispose();
    pdf.endDraw();
    if (verbose)
      println("saved PDF file");
    thickSlice=0;
    status="finished";
  }




  public void savePNG(int resolution)
  {    
    status="exporting PNG";
    PVector sliceBoundary=new PVector(0, 0);
    //figure out the bounding dimensions of a slice
    switch(axis)
    {
      case('x'):
      sliceBoundary=new PVector(bound.y, bound.z);
      break;
      case('y'):
      sliceBoundary=new PVector(bound.x, bound.z);
      break;
      case('z'):
      sliceBoundary=new PVector(bound.x, bound.y);
      break;
    }
    float scalingFactor=(float)resolution/25.4f;
    PGraphics output=createGraphics((int)(sliceBoundary.x*scalingFactor), (int)(sliceBoundary.y*scalingFactor));
    for (thickSlice=0;thickSlice<slices.size()/microslices;thickSlice++)
    {
      output.beginDraw();
      output.background(255,1.0f);
      output.strokeWeight(1.0f);
      if (verbose)
        println("drawing slice " + thickSlice +" / "+ slices.size()/microslices);
      for (slice=thickSlice*microslices;(slice<((thickSlice+1)*microslices)+(int)(overlap*microslices))&&(slice<slicer.slices.size());slice++)
      {
        for (int i=0;i<((Slice)slices.get(slice)).segments.size();i++)
        {
          Line segment=((Line)((Slice)slices.get(slice)).segments.get(i));
          output.beginShape();
          output.stroke(segment.start.vertexColor);
          output.vertex(segment.start.point.x*scalingFactor/displayScalingFactor, segment.start.point.y*scalingFactor/displayScalingFactor);           
          output.stroke(segment.end.vertexColor);
          output.vertex(segment.end.point.x*scalingFactor/displayScalingFactor, segment.end.point.y*scalingFactor/displayScalingFactor);          
          output.endShape();
        }
      }
      output.endDraw();
      if (((Slice)slices.get(0)).segments.size()>0)
      {
        Line segment=((Line)((Slice)slices.get(slice)).segments.get(0));
        println(segment.start.point.x+" "+segment.start.point.y);
      }
      String sliceString=nf(thickSlice, ceil(log(slices.size()/microslices)/log(10)));
      output.save(directory+file+"-"+axis+"-axis-"+microslices+"-microslices-"+sliceString+".png");
    }
    status="finished";
  }

  public void drawCrosshatches(PGraphics pdf, int x, int y, int w, int h, int length, int slice)
  {
    pdf.fill(0);
    pdf.strokeWeight(1);
    pdf.stroke(0);
    pdf.line(x, y, x+length, y);
    pdf.line(x, y, x, y+length);
    pdf.line(x+w, y, x+w-length, y);
    pdf.line(x+w, y, x+w, y+length);
    pdf.line(x, y+h, x, y+h-length);
    pdf.line(x, y+h, x+length, y+h);
    pdf.line(x+w, y+h, x+w-length, y+h);
    pdf.line(x+w, y+h, x+w, y+h-length);
    String output=slice+"/"+slices.size()/microslices;
    pdf.textSize(10);
    pdf.text(output, x+w-pdf.textWidth(output), y+10);
  }



  public void loadPLY(String filename)
  {
    status="loading PLY file";
    if (verbose)
      println("loading file:  "+filename);
    //first, check to make sure it really is a PLY file
    String [] parts=split(filename, '.');
    int propertyIndex=0;
    if (parts.length > 1)
      if (parts[1].toLowerCase().equals("ply"))
      {
        String[] lines=loadStrings(filename);
        if (lines[0].toLowerCase().equals("ply"))  //the first line in the file should say 'ply'
        {
          int index=1;
          ArrayList elements=new ArrayList<Element>();
          while (!lines[index].toLowerCase ().equals("end_header")) //go through the end of the header
          {
            lines[index]=lines[index].toLowerCase();
            parts=split(lines[index], ' ');
            if (parts[0].equals("element"))
            {
              if (verbose)
                println("Hey, it's an element");
              propertyIndex=index;
              elements.add(new Element(parts[1], PApplet.parseInt(parts[2])));
            }
            else if (parts[0].equals("property"))
            {
              Element element=(Element)elements.get(elements.size()-1);
              element.properties.add(new Property(index-propertyIndex-1, parts[2]));
              elements.set(elements.size()-1, element);
              if (verbose)
                println("looks like "+parts[2]+" is at index "+(index-propertyIndex-1));
            }
            else
            {
              if (verbose)
                println("don't know what to do for "+lines[index]);
            }
            index++;
          }
          if (verbose)
            println("well, that's the end of the header.  Let's get down to brass tacks");   
          index++;
          for (int i=0;i<elements.size();i++)
          {
            if (((Element)elements.get(i)).name.equals("vertex"))
            {
              int redIndex=0, greenIndex=0, blueIndex=0;
              Element element=(Element)elements.get(i);
              for (int j=0;j<element.properties.size();j++)
              {
                if (((Property)element.properties.get(j)).name.toLowerCase().equals("red"))
                  redIndex=((Property)element.properties.get(j)).index;
                if (((Property)element.properties.get(j)).name.toLowerCase().equals("green"))
                  greenIndex=((Property)element.properties.get(j)).index;
                if (((Property)element.properties.get(j)).name.toLowerCase().equals("blue"))
                  blueIndex=((Property)element.properties.get(j)).index;
              }
              println("found our color indices: "+redIndex+" "+greenIndex+" "+blueIndex);
              for (int j=0;j<((Element)elements.get(i)).units;j++)
              {
                parts=split(lines[index], ' ');
                if (parts.length==3)
                  vertices.add(new Vertex(PApplet.parseFloat(parts[0]), PApplet.parseFloat(parts[1]), PApplet.parseFloat(parts[2])));
                else if (parts.length>=6)
                  vertices.add(new Vertex(PApplet.parseFloat(parts[0]), PApplet.parseFloat(parts[1]), PApplet.parseFloat(parts[2]), PApplet.parseInt(parts[redIndex]), PApplet.parseInt(parts[greenIndex]), PApplet.parseInt(parts[blueIndex])));
                index++;
              }
            }
            else if (((Element)elements.get(i)).name.equals("face"))
            {
              for (int j=0;j<((Element)elements.get(i)).units;j++)
              {
                parts=split(lines[index], ' ');
                if (parts.length==4)
                  triangles.add(new Triangle(PApplet.parseInt(parts[1]), PApplet.parseInt(parts[2]), PApplet.parseInt(parts[3])));
                else if (parts.length==5)
                {
                  triangles.add(new Triangle(PApplet.parseInt(parts[1]), PApplet.parseInt(parts[2]), PApplet.parseInt(parts[3])));
                  triangles.add(new Triangle(PApplet.parseInt(parts[2]), PApplet.parseInt(parts[3]), PApplet.parseInt(parts[4])));
                }
                else if (parts.length>5)  //in case there is rando extra information here, like face colors or normals
                  triangles.add(new Triangle(PApplet.parseInt(parts[1]), PApplet.parseInt(parts[2]), PApplet.parseInt(parts[3])));

                //TODO -- actually parse the list of element attributes and figure out what the data actually means
                // not just guess at it based on the length
                index++;
              }
            }
          }
          if (verbose)
            println("loaded all vertices and faces");
          println(vertices.size() + " vertices");
          println(triangles.size() + " triangles");
        }

        else
        {
          if (verbose)
            println("error -- "+filename+" is not a valid PLY file");
          error=true;
          status="not a PLY file";
        }
      }
    status="file loaded";
  }

  public void scaleModel()
  {
    status="scaling model";
    min=new Vertex(1000, 1000, 1000);
    max=new Vertex(-1000, -1000, -1000); 
    for (int i=0;i<vertices.size();i++)
    {
      Vertex vertex=(Vertex)vertices.get(i);
      if (vertex.point.x>max.point.x)
        max.point.x=vertex.point.x;
      if (vertex.point.y>max.point.y)
        max.point.y=vertex.point.y;
      if (vertex.point.z>max.point.z)
        max.point.z=vertex.point.z;

      if (vertex.point.x<min.point.x)
        min.point.x=vertex.point.x;
      if (vertex.point.y<min.point.y)
        min.point.y=vertex.point.y;
      if (vertex.point.z<min.point.z)
        min.point.z=vertex.point.z;
    }
    println("the model's bounding dimensions are "+(max.point.x-min.point.x)+" x "+ (max.point.y-min.point.y) +" x "+ (max.point.z-min.point.z)); 
    float[] scalingFactors= {
      bound.x/(max.point.x-min.point.x), bound.y/(max.point.y-min.point.y), bound.z/(max.point.z-min.point.z)
      };
      float scalingFactor=sort(scalingFactors)[0]*(1-margins);
    println("scaling factor is: "+scalingFactor);
    for (int i=0;i<vertices.size();i++)
    {
      Vertex vertex=(Vertex)vertices.get(i);
      vertex.scale(scalingFactor);
      vertices.set(i, vertex);
    }
    if (verbose)
      println("mesh is scaled to the bounding box");
    min.scale(scalingFactor);
    max.scale(scalingFactor);
    for (int i=0;i<vertices.size();i++)
    {
      Vertex vertex=(Vertex)vertices.get(i);
      vertex.point.x-=min.point.x;
      vertex.point.y-=min.point.y;
      vertex.point.z-=min.point.z;
      vertex.point.x+=(bound.x-(max.point.x-min.point.x))/2;
      vertex.point.y+=(bound.y-(max.point.y-min.point.y))/2;
      vertex.point.z+=(bound.z-(max.point.z-min.point.z))/2;


      vertices.set(i, vertex);
    }

    //translate the max and min points, too 
    max.point.x-=min.point.x;
    max.point.y-=min.point.y;
    max.point.z-=min.point.z;
    max.point.x+=(bound.x-(max.point.x-min.point.x))/2;
    max.point.y+=(bound.y-(max.point.y-min.point.y))/2;
    max.point.z+=(bound.z-(max.point.z-min.point.z))/2;

    min.point.x-=min.point.x;
    min.point.y-=min.point.y;
    min.point.z-=min.point.z;
    min.point.x+=(bound.x-(max.point.x-min.point.x))/2;
    min.point.y+=(bound.y-(max.point.y-min.point.y))/2;
    min.point.z+=(bound.z-(max.point.z-min.point.z))/2;


    if (verbose)
      println("mesh is centered within the bounding box");
    status="model scaled";
  }

  public float getAxis(PVector vector, char axis)
  {
    switch(axis)
    {
      case('x'):
      return vector.x;
      case('y'):
      return vector.y;
      case('z'):
      return vector.z;
    default:
      return 0;
    }
  }

  public void sliceModel(float thickness, char axis)
  {
    status="slicing";
    startTime=millis();
    slices.clear(); 
    thickness/=microslices; 
    float plane=0;
    for (plane=0;plane<getAxis(bound,axis);plane+=thickness)
    {
      slices.add(new Slice());
      int intersections=0;
      for (int i=0;i<triangles.size();i++)
      {
        if (crossesPlane((Triangle)triangles.get(i), axis, plane))
        {
          Line a=getSegment((Triangle)triangles.get(i), axis, plane);
          ((Slice)slices.get(slices.size()-1)).segments.add(a);
          intersections++;
        }
      }
      if (verbose)
        println("sliced plane "+plane+" / " + getAxis(bound, axis)+".  "+intersections+" intersections");
    }
    status="sliced";
  }

  public boolean crossesPlane(Triangle triangle, char axis, float plane)
  {
    Vertex[] triangleVertices=new Vertex[3];
    for (int i=0;i<3;i++)
      triangleVertices[i]=(Vertex)vertices.get(triangle.vertices[i]);
    return (!(( (triangleVertices[0].getAxis(axis) > plane) && (triangleVertices[1].getAxis(axis) > plane) && (triangleVertices[2].getAxis(axis) > plane)) || ((triangleVertices[0].getAxis(axis) < plane) && (triangleVertices[1].getAxis(axis) < plane) && (triangleVertices[2].getAxis(axis) < plane))));
  }

  public Line getSegment(Triangle triangle, char axis, float plane)
  {
    Vertex[] triangleVertices=new Vertex[3];
    for (int i=0;i<3;i++)
      triangleVertices[i]=(Vertex)vertices.get(triangle.vertices[i]);
    Vertex a, b;
    if (triangleVertices[0].getAxis(axis)>plane)
    {
      if (triangleVertices[1].getAxis(axis)>plane)
      {
        a=intersectLineWithPlane(triangleVertices[0], triangleVertices[2], plane, axis);
        b=intersectLineWithPlane(triangleVertices[1], triangleVertices[2], plane, axis);
      }
      else
      {
        if (triangleVertices[2].getAxis(axis)>plane)
        {
          a=intersectLineWithPlane(triangleVertices[0], triangleVertices[1], plane, axis);
          b=intersectLineWithPlane(triangleVertices[2], triangleVertices[1], plane, axis);
        }
        else
        {
          a=intersectLineWithPlane(triangleVertices[0], triangleVertices[1], plane, axis);
          b=intersectLineWithPlane(triangleVertices[0], triangleVertices[2], plane, axis);
        }
      }
    }
    else
    {
      if (triangleVertices[1].getAxis(axis)<plane)
      {
        a=intersectLineWithPlane(triangleVertices[2], triangleVertices[0], plane, axis);
        b=intersectLineWithPlane(triangleVertices[2], triangleVertices[1], plane, axis);
      }
      else
      {
        if (triangleVertices[2].getAxis(axis)<plane)
        {
          a=intersectLineWithPlane(triangleVertices[1], triangleVertices[0], plane, axis);
          b=intersectLineWithPlane(triangleVertices[1], triangleVertices[2], plane, axis);
        }
        else
        {
          a=intersectLineWithPlane(triangleVertices[1], triangleVertices[0], plane, axis);
          b=intersectLineWithPlane(triangleVertices[2], triangleVertices[0], plane, axis);
        }
      }
    }
    //  println(displayScalingFactor);
    return(new Line(a, b, axis, displayScalingFactor));
  }

  public Vertex intersectLineWithPlane(Vertex a, Vertex b, float plane, char axis)
  {

    PVector P2subP1=PVector.sub(b.point, a.point);
    PVector normal;
    switch(axis) {
      case('x'):
      normal=new PVector(1, 0, 0);
      break;
      case('y'):
      normal=new PVector(0, 1, 0);
      break;
      case('z'):
      normal=new PVector(0, 0, 1);
      break;
    default:
      normal=new PVector(0, 0, 0);
      break;
    }
    PVector P3=PVector.mult(normal, plane);
    PVector P3subP1=PVector.sub(P3, a.point);
    float u = normal.dot(P3subP1) / normal.dot(P2subP1);
    Vertex intersection = new Vertex(PVector.add(a.point, PVector.mult(P2subP1, u)));  
    intersection.averageColors(a, b);
    return intersection;
  }
}

//data structure for storing basic information about a PLY element
class Element {
  public
    String name;
  int units;
  ArrayList properties;

  public Element(String _name, int _units)
  {
    name=_name;
    units=_units;
    properties=new ArrayList<Property>();
  }
}

class Property {
  int index;
  String name;
  public Property(int _index, String _name)
  {
    index=_index;
    name=_name;
  }
}

class Line {
  Vertex start, end;
  public Line(Vertex _start, Vertex _end, char axis, float _scalingFactor)
  {
    switch(axis)
    {      
      case('x'):
      start= new Vertex(_scalingFactor*_start.point.y, _scalingFactor*_start.point.z, 0, _start.vertexColor);
      end= new Vertex(_scalingFactor*_end.point.y, _scalingFactor*_end.point.z, 0, _end.vertexColor);
      break;
      case('y'):
      start=new Vertex(_scalingFactor*_start.point.x, _scalingFactor*_start.point.z, 0, _start.vertexColor);
      end=new Vertex(_scalingFactor*_end.point.x, _scalingFactor*_end.point.z, 0, _end.vertexColor);
      break;
      case('z'):
      start=new Vertex(_scalingFactor*_start.point.x, _scalingFactor*_start.point.y, 0, _start.vertexColor);
      end=new Vertex(_scalingFactor*_end.point.x, _scalingFactor*_end.point.y, 0, _end.vertexColor);
      break;
    }
  }
  public void drawSegment(int segmentColor)
  {
    beginShape();
    if (segmentColor==255)
      stroke(255);
    else
      stroke(start.vertexColor);
    vertex(start.point.x, start.point.y);
    if (segmentColor==255)
      stroke(255);
    else
      stroke(end.vertexColor);
    vertex(end.point.x, end.point.y);
    endShape();
  }
  public void printLine()
  {
    start.printVertex();
    end.printVertex();
  }
}

class Slice {
  ArrayList segments;
  Slice()
  {
    segments=new ArrayList<Line>();
  }
}


class Vertex {
  int vertexColor;
  PVector point;
  public Vertex(float _x, float _y, float _z, int _color)
  {
    point=new PVector(_x, _y, _z);
    vertexColor=_color;
  }
  public Vertex(float _x, float _y, float _z)
  {
    point=new PVector(_x, _y, _z);
    vertexColor=color(0, 0, 0);
  }
  public Vertex(PVector _point)
  {
    point=_point;
    vertexColor=color(0, 0, 0);
  }
  public void averageColors(Vertex a, Vertex b)
  {
    float distance=a.point.dist(b.point);
    float distanceToA=point.dist(a.point);
    float ratio=distanceToA/distance;
    vertexColor=lerpColor(a.vertexColor, b.vertexColor, ratio);
  }
  public Vertex(float _x, float _y, float _z, int _r, int _g, int _b)
  {
    point=new PVector(_x, _y, _z);
    vertexColor=color(_r, _g, _b);
  }
  public void scale(float scalingFactor)
  {
    point.mult(scalingFactor);
  }

  public float getAxis(char axis)
  {

    switch(axis) {
      case('x'):
      return point.x;
      case('y'):
      return point.y;      
      case('z'):
      return point.z;
    default:
      return 0;
    }
  }
  public void printVertex() 
  { 
    println("( "+point.x+", "+point.y+", "+point.z+" )");
  }
}


class Triangle {
  int[] vertices;
  public Triangle(int _a, int _b, int _c)
  {
    vertices=new int[3];
    vertices[0]=_a;
    vertices[1]=_b;
    vertices[2]=_c;
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "glassCutter_1_5" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
