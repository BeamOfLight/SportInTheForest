package beamoflight.sportintheforest;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class KnowledgeActivity extends Activity {
    int page = 0;
    TextView tvKnowledgeGroupName, tvKnowledgeInfo;
    Button btPrevKnowledge, btNextKnowledge;
    ArrayList<KnowledgePage> pages;

    private class KnowledgePage
    {
        private String groupName;
        private String info;

        KnowledgePage(String group_name, String info_)
        {
            groupName = group_name;
            info = info_;
        }
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge);

        tvKnowledgeGroupName = findViewById(R.id.tvKnowledgeGroupName);
        tvKnowledgeInfo = findViewById(R.id.tvKnowledgeInfo);
        btPrevKnowledge = findViewById(R.id.btPrevKnowledge);
        btNextKnowledge = findViewById(R.id.btNextKnowledge);

        btPrevKnowledge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (page > 0) {
                    page--;
                    loadCurrentKnowledgePage();
                }
            }
        });

        btNextKnowledge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (page < pages.size() - 1) {
                    page++;
                    loadCurrentKnowledgePage();
                }
            }
        });

        initKnowledgeBase();
        loadCurrentKnowledgePage();
    }

    private void loadCurrentKnowledgePage()
    {
        tvKnowledgeGroupName.setText(pages.get(page).groupName);
        tvKnowledgeInfo.setText(pages.get(page).info);

        if (page > 0) {
            btPrevKnowledge.setVisibility(View.VISIBLE);
        } else {
            btPrevKnowledge.setVisibility(View.INVISIBLE);
        }

        if (page < pages.size() - 1) {
            btNextKnowledge.setVisibility(View.VISIBLE);
        } else {
            btNextKnowledge.setVisibility(View.INVISIBLE);
        }
    }

    private void initKnowledgeBase()
    {
        pages = new ArrayList<>();
        try {
            XmlPullParser xpp = getBaseContext().getResources().getXml(R.xml.knowledge);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("knowledge")) {
                            pages.add(
                                new KnowledgePage(
                                    xpp.getAttributeValue(null, "group"),
                                    xpp.getAttributeValue(null, "description")
                                )
                            );
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}