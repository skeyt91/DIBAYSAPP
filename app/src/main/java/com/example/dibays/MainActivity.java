package com.example.dibays;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int PRIMARY = Color.rgb(20, 35, 49);
    private static final int WHATSAPP = Color.rgb(22, 161, 99);
    private static final int LIGHT_GRAY = Color.rgb(244, 246, 248);
    private static final int ERROR = Color.rgb(201, 71, 67);
    private static final int TEXT_MUTED = Color.rgb(96, 108, 118);
    private static final int BORDER = Color.rgb(222, 228, 234);
    private static final int SURFACE = Color.rgb(250, 251, 252);
    private static final int INK = Color.rgb(12, 22, 32);
    private static final int SUCCESS_SOFT = Color.rgb(226, 244, 236);

    private EditText phoneInput;
    private CheckBox termsCheckBox;
    private Button continueButton;
    private LinearLayout phoneFieldContainer;
    private TextView countryBadgeText;
    private TextView countryCodeText;
    private Country selectedCountry = COUNTRIES[20];

    private static final Country[] COUNTRIES = new Country[]{
            new Country("AF", "Afganistan", "+93"),
            new Country("AL", "Albania", "+355"),
            new Country("DE", "Alemania", "+49"),
            new Country("AD", "Andorra", "+376"),
            new Country("AO", "Angola", "+244"),
            new Country("AG", "Antigua y Barbuda", "+1"),
            new Country("SA", "Arabia Saudita", "+966"),
            new Country("DZ", "Argelia", "+213"),
            new Country("AR", "Argentina", "+54"),
            new Country("AM", "Armenia", "+374"),
            new Country("AU", "Australia", "+61"),
            new Country("AT", "Austria", "+43"),
            new Country("AZ", "Azerbaiyan", "+994"),
            new Country("BS", "Bahamas", "+1"),
            new Country("BH", "Barein", "+973"),
            new Country("BD", "Bangladesh", "+880"),
            new Country("BB", "Barbados", "+1"),
            new Country("BE", "Belgica", "+32"),
            new Country("BZ", "Belice", "+501"),
            new Country("BJ", "Benin", "+229"),
            new Country("BO", "Bolivia", "+591"),
            new Country("BA", "Bosnia y Herzegovina", "+387"),
            new Country("BW", "Botsuana", "+267"),
            new Country("BR", "Brasil", "+55"),
            new Country("BN", "Brunei", "+673"),
            new Country("BG", "Bulgaria", "+359"),
            new Country("BF", "Burkina Faso", "+226"),
            new Country("BI", "Burundi", "+257"),
            new Country("BT", "Butan", "+975"),
            new Country("CV", "Cabo Verde", "+238"),
            new Country("KH", "Camboya", "+855"),
            new Country("CM", "Camerun", "+237"),
            new Country("CA", "Canada", "+1"),
            new Country("QA", "Catar", "+974"),
            new Country("TD", "Chad", "+235"),
            new Country("CL", "Chile", "+56"),
            new Country("CN", "China", "+86"),
            new Country("CY", "Chipre", "+357"),
            new Country("CO", "Colombia", "+57"),
            new Country("KM", "Comoras", "+269"),
            new Country("CG", "Congo", "+242"),
            new Country("KR", "Corea del Sur", "+82"),
            new Country("CR", "Costa Rica", "+506"),
            new Country("CI", "Costa de Marfil", "+225"),
            new Country("HR", "Croacia", "+385"),
            new Country("CU", "Cuba", "+53"),
            new Country("DK", "Dinamarca", "+45"),
            new Country("DM", "Dominica", "+1"),
            new Country("EC", "Ecuador", "+593"),
            new Country("EG", "Egipto", "+20"),
            new Country("SV", "El Salvador", "+503"),
            new Country("AE", "Emiratos Arabes Unidos", "+971"),
            new Country("ER", "Eritrea", "+291"),
            new Country("SK", "Eslovaquia", "+421"),
            new Country("SI", "Eslovenia", "+386"),
            new Country("ES", "Espana", "+34"),
            new Country("US", "Estados Unidos", "+1"),
            new Country("EE", "Estonia", "+372"),
            new Country("ET", "Etiopia", "+251"),
            new Country("PH", "Filipinas", "+63"),
            new Country("FI", "Finlandia", "+358"),
            new Country("FJ", "Fiyi", "+679"),
            new Country("FR", "Francia", "+33"),
            new Country("GA", "Gabon", "+241"),
            new Country("GM", "Gambia", "+220"),
            new Country("GE", "Georgia", "+995"),
            new Country("GH", "Ghana", "+233"),
            new Country("GD", "Granada", "+1"),
            new Country("GR", "Grecia", "+30"),
            new Country("GT", "Guatemala", "+502"),
            new Country("GN", "Guinea", "+224"),
            new Country("GQ", "Guinea Ecuatorial", "+240"),
            new Country("GW", "Guinea-Bisau", "+245"),
            new Country("GY", "Guyana", "+592"),
            new Country("HT", "Haiti", "+509"),
            new Country("HN", "Honduras", "+504"),
            new Country("HU", "Hungria", "+36"),
            new Country("IN", "India", "+91"),
            new Country("ID", "Indonesia", "+62"),
            new Country("IQ", "Irak", "+964"),
            new Country("IR", "Iran", "+98"),
            new Country("IE", "Irlanda", "+353"),
            new Country("IS", "Islandia", "+354"),
            new Country("IL", "Israel", "+972"),
            new Country("IT", "Italia", "+39"),
            new Country("JM", "Jamaica", "+1"),
            new Country("JP", "Japon", "+81"),
            new Country("JO", "Jordania", "+962"),
            new Country("KZ", "Kazajistan", "+7"),
            new Country("KE", "Kenia", "+254"),
            new Country("KG", "Kirguistan", "+996"),
            new Country("KI", "Kiribati", "+686"),
            new Country("KW", "Kuwait", "+965"),
            new Country("LA", "Laos", "+856"),
            new Country("LS", "Lesoto", "+266"),
            new Country("LV", "Letonia", "+371"),
            new Country("LB", "Libano", "+961"),
            new Country("LR", "Liberia", "+231"),
            new Country("LY", "Libia", "+218"),
            new Country("LI", "Liechtenstein", "+423"),
            new Country("LT", "Lituania", "+370"),
            new Country("LU", "Luxemburgo", "+352"),
            new Country("MK", "Macedonia del Norte", "+389"),
            new Country("MG", "Madagascar", "+261"),
            new Country("MY", "Malasia", "+60"),
            new Country("MW", "Malaui", "+265"),
            new Country("MV", "Maldivas", "+960"),
            new Country("ML", "Mali", "+223"),
            new Country("MT", "Malta", "+356"),
            new Country("MA", "Marruecos", "+212"),
            new Country("MU", "Mauricio", "+230"),
            new Country("MR", "Mauritania", "+222"),
            new Country("MX", "Mexico", "+52"),
            new Country("FM", "Micronesia", "+691"),
            new Country("MD", "Moldavia", "+373"),
            new Country("MC", "Monaco", "+377"),
            new Country("MN", "Mongolia", "+976"),
            new Country("ME", "Montenegro", "+382"),
            new Country("MZ", "Mozambique", "+258"),
            new Country("MM", "Myanmar", "+95"),
            new Country("NA", "Namibia", "+264"),
            new Country("NR", "Nauru", "+674"),
            new Country("NP", "Nepal", "+977"),
            new Country("NI", "Nicaragua", "+505"),
            new Country("NE", "Niger", "+227"),
            new Country("NG", "Nigeria", "+234"),
            new Country("NO", "Noruega", "+47"),
            new Country("NZ", "Nueva Zelanda", "+64"),
            new Country("OM", "Oman", "+968"),
            new Country("NL", "Paises Bajos", "+31"),
            new Country("PK", "Pakistan", "+92"),
            new Country("PW", "Palaos", "+680"),
            new Country("PA", "Panama", "+507"),
            new Country("PG", "Papua Nueva Guinea", "+675"),
            new Country("PY", "Paraguay", "+595"),
            new Country("PE", "Peru", "+51"),
            new Country("PL", "Polonia", "+48"),
            new Country("PT", "Portugal", "+351"),
            new Country("GB", "Reino Unido", "+44"),
            new Country("CF", "Republica Centroafricana", "+236"),
            new Country("CZ", "Republica Checa", "+420"),
            new Country("DO", "Republica Dominicana", "+1"),
            new Country("RW", "Ruanda", "+250"),
            new Country("RO", "Rumania", "+40"),
            new Country("RU", "Rusia", "+7"),
            new Country("WS", "Samoa", "+685"),
            new Country("SM", "San Marino", "+378"),
            new Country("LC", "Santa Lucia", "+1"),
            new Country("ST", "Santo Tome y Principe", "+239"),
            new Country("SN", "Senegal", "+221"),
            new Country("RS", "Serbia", "+381"),
            new Country("SC", "Seychelles", "+248"),
            new Country("SL", "Sierra Leona", "+232"),
            new Country("SG", "Singapur", "+65"),
            new Country("SY", "Siria", "+963"),
            new Country("SO", "Somalia", "+252"),
            new Country("LK", "Sri Lanka", "+94"),
            new Country("SZ", "Suazilandia", "+268"),
            new Country("ZA", "Sudafrica", "+27"),
            new Country("SD", "Sudan", "+249"),
            new Country("SS", "Sudan del Sur", "+211"),
            new Country("SE", "Suecia", "+46"),
            new Country("CH", "Suiza", "+41"),
            new Country("SR", "Surinam", "+597"),
            new Country("TH", "Tailandia", "+66"),
            new Country("TZ", "Tanzania", "+255"),
            new Country("TJ", "Tayikistan", "+992"),
            new Country("TL", "Timor Oriental", "+670"),
            new Country("TG", "Togo", "+228"),
            new Country("TO", "Tonga", "+676"),
            new Country("TT", "Trinidad y Tobago", "+1"),
            new Country("TN", "Tunez", "+216"),
            new Country("TM", "Turkmenistan", "+993"),
            new Country("TR", "Turquia", "+90"),
            new Country("TV", "Tuvalu", "+688"),
            new Country("UA", "Ucrania", "+380"),
            new Country("UG", "Uganda", "+256"),
            new Country("UY", "Uruguay", "+598"),
            new Country("UZ", "Uzbekistan", "+998"),
            new Country("VU", "Vanuatu", "+678"),
            new Country("VA", "Vaticano", "+379"),
            new Country("VE", "Venezuela", "+58"),
            new Country("VN", "Vietnam", "+84"),
            new Country("YE", "Yemen", "+967"),
            new Country("ZM", "Zambia", "+260"),
            new Country("ZW", "Zimbabue", "+263")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        showWelcomeScreen();
    }

    private void showWelcomeScreen() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        root.addView(content, fullSize());

        HeroBusinessView hero = new HeroBusinessView(this);
        content.addView(hero, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(420)
        ));

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(24), dp(30), dp(24), dp(28));
        card.setBackground(roundedTop(Color.WHITE, 32));
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
        );
        root.addView(card, cardParams);

        TextView title = title("Administra tus fardos fácil y seguro", 30);
        card.addView(title);

        TextView subtitle = body("Controla tus ventas, deudas, clientes, proveedores e inventario desde tu celular.", 16);
        LinearLayout.LayoutParams subtitleParams = marginTop(12);
        card.addView(subtitle, subtitleParams);

        Button start = primaryButton("Empezar");
        start.setOnClickListener(v -> showRegisterScreen());
        card.addView(start, marginTop(26));

        Button login = outlineButton("Ya tengo una cuenta");
        login.setOnClickListener(v -> showLoginScreen());
        card.addView(login, marginTop(12));

        setContentView(root);
    }

    private void showRegisterScreen() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(22), dp(28), dp(22), dp(120));
        scroll.addView(content, fullSize());
        root.addView(scroll, fullSize());

        content.addView(topBackRow(v -> showWelcomeScreen()));
        content.addView(progressBar());

        TextView title = title("Tu información siempre segura", 28);
        content.addView(title, marginTop(42));
        content.addView(body("Accede con tu número de celular, sin contraseñas complicadas.", 16), marginTop(12));

        TextView label = label("Ingresa tu celular *");
        content.addView(label, marginTop(36));

        phoneFieldContainer = new LinearLayout(this);
        phoneFieldContainer.setOrientation(LinearLayout.HORIZONTAL);
        phoneFieldContainer.setGravity(Gravity.CENTER_VERTICAL);
        phoneFieldContainer.setPadding(dp(14), dp(4), dp(14), dp(4));
        phoneFieldContainer.setBackground(roundedStroke(Color.WHITE, BORDER, 18, 1));

        LinearLayout countrySelector = new LinearLayout(this);
        countrySelector.setGravity(Gravity.CENTER_VERTICAL);
        countrySelector.setPadding(0, 0, dp(8), 0);
        countrySelector.setOnClickListener(v -> showCountryPicker());

        countryBadgeText = text(selectedCountry.iso, 12, Color.WHITE, true);
        countryBadgeText.setGravity(Gravity.CENTER);
        countryBadgeText.setBackground(rounded(PRIMARY, 8));
        countrySelector.addView(countryBadgeText, new LinearLayout.LayoutParams(dp(34), dp(24)));

        countryCodeText = text(selectedCountry.dialCode + " v", 16, PRIMARY, true);
        LinearLayout.LayoutParams codeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        codeParams.setMargins(dp(10), 0, dp(10), 0);
        countrySelector.addView(countryCodeText, codeParams);
        phoneFieldContainer.addView(countrySelector);

        View divider = new View(this);
        divider.setBackgroundColor(BORDER);
        phoneFieldContainer.addView(divider, new LinearLayout.LayoutParams(dp(1), dp(28)));

        phoneInput = new EditText(this);
        phoneInput.setHint("Número celular");
        phoneInput.setTextSize(16);
        phoneInput.setSingleLine(true);
        phoneInput.setTextColor(PRIMARY);
        phoneInput.setHintTextColor(Color.rgb(142, 151, 160));
        phoneInput.setBackgroundColor(Color.TRANSPARENT);
        phoneInput.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateRegisterState(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        phoneFieldContainer.addView(phoneInput, new LinearLayout.LayoutParams(0, dp(54), 1));
        content.addView(phoneFieldContainer, marginTop(8));

        termsCheckBox = new CheckBox(this);
        termsCheckBox.setText("Acepto los términos y condiciones");
        termsCheckBox.setTextColor(TEXT_MUTED);
        termsCheckBox.setTextSize(14);
        termsCheckBox.setButtonTintList(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{PRIMARY, Color.rgb(156, 166, 176)}
        ));
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateRegisterState(false));
        content.addView(termsCheckBox, marginTop(18));

        continueButton = primaryButton("Continuar");
        continueButton.setOnClickListener(v -> {
            if (!isRegisterValid()) {
                updateRegisterState(true);
                return;
            }
            registerWithSupabase();
        });
        FrameLayout.LayoutParams bottomButtonParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(58),
                Gravity.BOTTOM
        );
        bottomButtonParams.setMargins(dp(22), 0, dp(22), dp(24));
        root.addView(continueButton, bottomButtonParams);

        updateRegisterState(true);
        setContentView(root);
    }

    private void showLoginScreen() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(SURFACE);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root.addView(scroll, fullSize());

        boolean wide = getResources().getConfiguration().screenWidthDp >= 720;
        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(wide ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        shell.setGravity(Gravity.CENTER);
        shell.setPadding(
                wide ? dp(44) : dp(22),
                wide ? dp(34) : dp(24),
                wide ? dp(44) : dp(22),
                wide ? dp(34) : dp(96)
        );
        scroll.addView(shell, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        LinearLayout brandPanel = new LinearLayout(this);
        brandPanel.setOrientation(LinearLayout.VERTICAL);
        brandPanel.setPadding(dp(28), dp(28), dp(28), dp(28));
        brandPanel.setGravity(Gravity.CENTER_VERTICAL);
        brandPanel.setBackground(roundedStroke(Color.WHITE, BORDER, 22, 1));

        TextView eyebrow = text("DIBAYS FARDOS", 13, WHATSAPP, true);
        eyebrow.setLetterSpacing(0.08f);
        brandPanel.addView(eyebrow);

        TextView headline = text("Ingreso seguro para tu negocio", wide ? 34 : 28, INK, true);
        headline.setLineSpacing(dp(3), 1.0f);
        brandPanel.addView(headline, marginTop(10));

        TextView summary = body("Gestiona inventario, ventas y deudas con una experiencia limpia, rapida y protegida.", 16);
        brandPanel.addView(summary, marginTop(12));

        LoginIllustrationView financePanel = new LoginIllustrationView(this);
        LinearLayout.LayoutParams financeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                wide ? dp(300) : dp(210)
        );
        financeParams.setMargins(0, dp(28), 0, wide ? dp(24) : dp(10));
        brandPanel.addView(financePanel, financeParams);

        if (wide) {
            brandPanel.addView(trustRow("Control diario", "Ventas, deudas e inventario"));
            brandPanel.addView(trustRow("Acceso rapido", "Con numero de celular"));
        }

        LinearLayout.LayoutParams brandParams = new LinearLayout.LayoutParams(
                wide ? 0 : ViewGroup.LayoutParams.MATCH_PARENT,
                wide ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT,
                wide ? 1.05f : 0
        );
        if (wide) {
            brandParams.setMargins(0, 0, dp(22), 0);
        }
        shell.addView(brandPanel, brandParams);

        LinearLayout loginPanel = new LinearLayout(this);
        loginPanel.setOrientation(LinearLayout.VERTICAL);
        loginPanel.setPadding(dp(28), dp(26), dp(28), dp(26));
        loginPanel.setBackground(roundedStroke(Color.WHITE, BORDER, 22, 1));

        loginPanel.addView(topBackRow(v -> showWelcomeScreen()));

        TextView title = title("Iniciar sesion", wide ? 32 : 30);
        loginPanel.addView(title, marginTop(28));

        TextView subtitle = body("Elige un metodo de acceso para continuar con DIBAYS FARDOS.", 15);
        loginPanel.addView(subtitle, marginTop(8));

        LinearLayout secureNotice = new LinearLayout(this);
        secureNotice.setOrientation(LinearLayout.HORIZONTAL);
        secureNotice.setGravity(Gravity.CENTER_VERTICAL);
        secureNotice.setPadding(dp(14), dp(12), dp(14), dp(12));
        secureNotice.setBackground(roundedStroke(SUCCESS_SOFT, Color.rgb(190, 226, 209), 16, 1));

        TextView secureIcon = text("OK", 12, WHATSAPP, true);
        secureIcon.setGravity(Gravity.CENTER);
        secureIcon.setBackground(rounded(Color.WHITE, 10));
        secureNotice.addView(secureIcon, new LinearLayout.LayoutParams(dp(34), dp(28)));

        TextView secureText = text("Conexion protegida para informacion comercial sensible.", 14, Color.rgb(38, 92, 68), false);
        LinearLayout.LayoutParams secureTextParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        secureTextParams.setMargins(dp(12), 0, 0, 0);
        secureNotice.addView(secureText, secureTextParams);
        loginPanel.addView(secureNotice, marginTop(22));

        Button phoneLogin = primaryButton("Ingresar con numero celular");
        phoneLogin.setOnClickListener(v -> showRegisterScreen());
        loginPanel.addView(phoneLogin, marginTop(24));

        Button googleLogin = outlineButton("Ingresar con Google");
        googleLogin.setOnClickListener(v -> showAccountsScreen());
        loginPanel.addView(googleLogin, marginTop(12));

        TextView register = text("No tienes una cuenta? Registrate", 14, TEXT_MUTED, false);
        register.setGravity(Gravity.CENTER);
        register.setOnClickListener(v -> showRegisterScreen());
        loginPanel.addView(register, marginTop(22));

        View divider = new View(this);
        divider.setBackgroundColor(BORDER);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1)
        );
        dividerParams.setMargins(0, dp(24), 0, dp(18));
        loginPanel.addView(divider, dividerParams);

        TextView footer = text("Soporte disponible para recuperar el acceso a tu cuenta.", 13, TEXT_MUTED, false);
        footer.setGravity(Gravity.CENTER);
        loginPanel.addView(footer);

        LinearLayout.LayoutParams loginParams = new LinearLayout.LayoutParams(
                wide ? 0 : ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                wide ? 0.95f : 0
        );
        if (!wide) {
            loginParams.setMargins(0, dp(16), 0, 0);
        }
        shell.addView(loginPanel, loginParams);

        setContentView(root);
    }

    private void showAccountsScreen() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(SURFACE);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(22), dp(28), dp(22), dp(28));
        root.addView(content, fullSize());

        content.addView(topBackRow(v -> showLoginScreen()));

        TextView title = title("Cuentas", 34);
        content.addView(title, marginTop(32));

        TextView subtitle = body("Selecciona o administra las cuentas de tu negocio.", 16);
        content.addView(subtitle, marginTop(8));

        LinearLayout accountCard = new LinearLayout(this);
        accountCard.setOrientation(LinearLayout.VERTICAL);
        accountCard.setPadding(dp(18), dp(18), dp(18), dp(18));
        accountCard.setBackground(roundedStroke(Color.WHITE, BORDER, 20, 1));
        content.addView(accountCard, marginTop(28));

        TextView badge = text("DIBAYS FARDOS", 13, WHATSAPP, true);
        accountCard.addView(badge);
        accountCard.addView(text("Cuenta principal", 22, INK, true), marginTop(8));
        accountCard.addView(body("Inventario, ventas, clientes, proveedores y deudas.", 14), marginTop(6));

        Button enter = primaryButton("Entrar");
        accountCard.addView(enter, marginTop(18));

        setContentView(root);
    }

    private LinearLayout trustRow(String title, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(8), 0, dp(8));

        TextView dot = text("", 1, WHATSAPP, true);
        dot.setBackground(rounded(WHATSAPP, 5));
        row.addView(dot, new LinearLayout.LayoutParams(dp(10), dp(10)));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams copyParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        copyParams.setMargins(dp(12), 0, 0, 0);
        row.addView(copy, copyParams);

        copy.addView(text(title, 14, INK, true));
        copy.addView(text(value, 13, TEXT_MUTED, false));
        return row;
    }

    private LinearLayout topBackRow(View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("‹", 34, PRIMARY, true);
        back.setGravity(Gravity.CENTER);
        back.setOnClickListener(listener);
        row.addView(back, new LinearLayout.LayoutParams(dp(42), dp(42)));
        return row;
    }

    private View progressBar() {
        LinearLayout track = new LinearLayout(this);
        track.setBackground(rounded(LIGHT_GRAY, 6));
        LinearLayout.LayoutParams trackParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(6)
        );
        trackParams.setMargins(0, dp(12), 0, 0);
        track.setLayoutParams(trackParams);

        View progress = new View(this);
        progress.setBackground(rounded(PRIMARY, 6));
        track.addView(progress, new LinearLayout.LayoutParams(0, dp(6), 0.45f));
        View rest = new View(this);
        track.addView(rest, new LinearLayout.LayoutParams(0, dp(6), 0.55f));
        return track;
    }

    private void showCountryPicker() {
        String[] countryItems = new String[COUNTRIES.length];
        for (int i = 0; i < COUNTRIES.length; i++) {
            countryItems[i] = COUNTRIES[i].iso + "  " + COUNTRIES[i].name + "  " + COUNTRIES[i].dialCode;
        }

        new AlertDialog.Builder(this)
                .setTitle("Selecciona tu pais")
                .setItems(countryItems, (dialog, which) -> {
                    selectedCountry = COUNTRIES[which];
                    countryBadgeText.setText(selectedCountry.iso);
                    countryCodeText.setText(selectedCountry.dialCode + " v");
                    phoneInput.requestFocus();
                    updateRegisterState(false);
                })
                .show();
    }

    private void updateRegisterState(boolean showErrors) {
        if (phoneInput == null || termsCheckBox == null || continueButton == null) {
            return;
        }

        boolean hasPhone = !phoneInput.getText().toString().trim().isEmpty();
        boolean valid = isRegisterValid();
        int stroke = showErrors && !hasPhone ? ERROR : BORDER;
        phoneFieldContainer.setBackground(roundedStroke(Color.WHITE, stroke, 18, showErrors && !hasPhone ? 2 : 1));

        continueButton.setEnabled(valid);
        if (valid) {
            continueButton.setTextColor(Color.WHITE);
            continueButton.setBackground(rounded(PRIMARY, 18));
        } else {
            continueButton.setTextColor(Color.rgb(133, 144, 154));
            continueButton.setBackground(rounded(Color.rgb(226, 231, 236), 18));
        }
    }

    private boolean isRegisterValid() {
        return phoneInput != null
                && termsCheckBox != null
                && !phoneInput.getText().toString().trim().isEmpty()
                && termsCheckBox.isChecked();
    }

    private void registerWithSupabase() {
        String phone = phoneInput.getText().toString().trim();
        String countryCode = selectedCountry.dialCode;
        continueButton.setEnabled(false);
        continueButton.setText("Conectando...");

        new Thread(() -> {
            try {
                new SupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY)
                        .registerUser(phone, countryCode);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cuenta conectada con Supabase", Toast.LENGTH_SHORT).show();
                    showAccountsScreen();
                });
            } catch (Exception exception) {
                runOnUiThread(() -> {
                    continueButton.setText("Continuar");
                    updateRegisterState(false);
                    Toast.makeText(this, "No se pudo conectar: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private TextView title(String value, int size) {
        TextView view = text(value, size, PRIMARY, true);
        view.setLineSpacing(dp(2), 1.0f);
        return view;
    }

    private TextView body(String value, int size) {
        TextView view = text(value, size, TEXT_MUTED, false);
        view.setLineSpacing(dp(4), 1.0f);
        return view;
    }

    private TextView label(String value) {
        return text(value, 14, PRIMARY, true);
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setIncludeFontPadding(true);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return view;
    }

    private Button primaryButton(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(16);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setBackground(rounded(PRIMARY, 18));
        button.setMinHeight(dp(58));
        return button;
    }

    private Button outlineButton(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(16);
        button.setTextColor(PRIMARY);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setBackground(roundedStroke(Color.WHITE, BORDER, 18, 1));
        button.setMinHeight(dp(58));
        return button;
    }

    private Button whatsappButton(String value) {
        Button button = new Button(this);
        button.setText("☎  " + value);
        button.setTextSize(14);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setPadding(dp(16), 0, dp(16), 0);
        button.setBackground(rounded(WHATSAPP, 26));
        return button;
    }

    private GradientDrawable rounded(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private GradientDrawable roundedStroke(int color, int strokeColor, int radiusDp, int strokeDp) {
        GradientDrawable drawable = rounded(color, radiusDp);
        drawable.setStroke(dp(strokeDp), strokeColor);
        return drawable;
    }

    private GradientDrawable roundedTop(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        float radius = dp(radiusDp);
        drawable.setCornerRadii(new float[]{radius, radius, radius, radius, 0, 0, 0, 0});
        return drawable;
    }

    private LinearLayout.LayoutParams marginTop(int topDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(topDp), 0, 0);
        return params;
    }

    private FrameLayout.LayoutParams fullSize() {
        return new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static class Country {
        final String iso;
        final String name;
        final String dialCode;

        Country(String iso, String name, String dialCode) {
            this.iso = iso;
            this.name = name;
            this.dialCode = dialCode;
        }
    }

    private static class HeroBusinessView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        HeroBusinessView(android.content.Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(LIGHT_GRAY);
            canvas.drawRect(0, 0, w, h, paint);

            paint.setColor(Color.rgb(226, 233, 239));
            canvas.drawCircle(w * 0.78f, h * 0.20f, w * 0.22f, paint);
            canvas.drawCircle(w * 0.20f, h * 0.34f, w * 0.14f, paint);

            paint.setColor(PRIMARY);
            RectF shop = new RectF(w * 0.16f, h * 0.42f, w * 0.84f, h * 0.74f);
            canvas.drawRoundRect(shop, 28, 28, paint);

            paint.setColor(Color.WHITE);
            RectF awning = new RectF(w * 0.20f, h * 0.35f, w * 0.80f, h * 0.47f);
            canvas.drawRoundRect(awning, 22, 22, paint);

            paint.setColor(Color.rgb(217, 226, 233));
            for (int i = 0; i < 4; i++) {
                float left = w * 0.23f + i * w * 0.13f;
                canvas.drawRoundRect(new RectF(left, h * 0.38f, left + w * 0.08f, h * 0.46f), 12, 12, paint);
            }

            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(new RectF(w * 0.26f, h * 0.52f, w * 0.48f, h * 0.72f), 16, 16, paint);
            canvas.drawRoundRect(new RectF(w * 0.54f, h * 0.52f, w * 0.74f, h * 0.63f), 16, 16, paint);

            paint.setColor(WHATSAPP);
            canvas.drawRoundRect(new RectF(w * 0.56f, h * 0.66f, w * 0.76f, h * 0.73f), 14, 14, paint);

            paint.setColor(Color.rgb(201, 71, 67));
            Path tag = new Path();
            tag.moveTo(w * 0.62f, h * 0.25f);
            tag.lineTo(w * 0.80f, h * 0.31f);
            tag.lineTo(w * 0.73f, h * 0.43f);
            tag.lineTo(w * 0.55f, h * 0.37f);
            tag.close();
            canvas.drawPath(tag, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(w * 0.08f);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText("%", w * 0.64f, h * 0.36f, paint);
        }
    }

    private static class LoginIllustrationView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        LoginIllustrationView(android.content.Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float radius = Math.min(w, h) * 0.08f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(SURFACE);
            canvas.drawRoundRect(new RectF(0, 0, w, h), radius, radius, paint);

            paint.setColor(Color.WHITE);
            RectF dashboard = new RectF(w * 0.08f, h * 0.12f, w * 0.92f, h * 0.88f);
            canvas.drawRoundRect(dashboard, 30, 30, paint);

            paint.setColor(PRIMARY);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            canvas.drawRoundRect(dashboard, 30, 30, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(PRIMARY);
            canvas.drawRoundRect(new RectF(w * 0.14f, h * 0.20f, w * 0.38f, h * 0.26f), 8, 8, paint);

            paint.setColor(Color.rgb(214, 222, 230));
            canvas.drawRoundRect(new RectF(w * 0.14f, h * 0.34f, w * 0.86f, h * 0.37f), 5, 5, paint);
            canvas.drawRoundRect(new RectF(w * 0.14f, h * 0.47f, w * 0.76f, h * 0.50f), 5, 5, paint);
            canvas.drawRoundRect(new RectF(w * 0.14f, h * 0.60f, w * 0.82f, h * 0.63f), 5, 5, paint);

            paint.setColor(WHATSAPP);
            canvas.drawRoundRect(new RectF(w * 0.14f, h * 0.73f, w * 0.48f, h * 0.80f), 14, 14, paint);

            paint.setColor(PRIMARY);
            canvas.drawCircle(w * 0.78f, h * 0.24f, w * 0.055f, paint);

            paint.setColor(SUCCESS_SOFT);
            canvas.drawRoundRect(new RectF(w * 0.56f, h * 0.68f, w * 0.86f, h * 0.82f), 18, 18, paint);
            paint.setColor(WHATSAPP);
            canvas.drawRoundRect(new RectF(w * 0.61f, h * 0.73f, w * 0.82f, h * 0.76f), 8, 8, paint);
        }
    }

    private static class BoliviaFlagView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        BoliviaFlagView(android.content.Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float stripe = getHeight() / 3f;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(213, 43, 30));
            canvas.drawRect(0, 0, getWidth(), stripe, paint);
            paint.setColor(Color.rgb(247, 226, 66));
            canvas.drawRect(0, stripe, getWidth(), stripe * 2, paint);
            paint.setColor(Color.rgb(0, 121, 52));
            canvas.drawRect(0, stripe * 2, getWidth(), getHeight(), paint);
        }
    }
}
