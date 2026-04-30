package com.example.dibays;

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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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

    private EditText nameInput;
    private EditText pinInput;
    private EditText pinConfirmInput;
    private CheckBox termsCheckBox;
    private Button continueButton;

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

        TextView title = title("Administra tus fardos facil y seguro", 30);
        card.addView(title);

        TextView subtitle = body("Controla tus ventas, deudas, clientes, proveedores e inventario desde tu negocio.", 16);
        LinearLayout.LayoutParams subtitleParams = marginTop(12);
        card.addView(subtitle, subtitleParams);

        Button start = primaryButton("Empezar");
        start.setOnClickListener(v -> showRegisterScreen());
        card.addView(start, marginTop(26));

        Button login = outlineButton("Registrar nombre y PIN");
        login.setOnClickListener(v -> showRegisterScreen());
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

        TextView eyebrow = text("REGISTRO SEGURO", 12, Color.rgb(126, 132, 145), true);
        eyebrow.setLetterSpacing(0.12f);
        content.addView(eyebrow, marginTop(32));

        TextView title = text("Crea tu cuenta segura", 32, Color.rgb(119, 86, 255), true);
        content.addView(title, marginTop(8));
        TextView subtitle = body("Registra tu nombre y confirma tu PIN para que no lo olvides.", 16);
        subtitle.setGravity(Gravity.START);
        content.addView(subtitle, marginTop(12));

        LinearLayout formCard = new LinearLayout(this);
        formCard.setOrientation(LinearLayout.VERTICAL);
        formCard.setPadding(dp(18), dp(18), dp(18), dp(18));
        formCard.setBackground(roundedStroke(Color.WHITE, BORDER, 24, 1));
        content.addView(formCard, marginTop(26));

        nameInput = registerInput("Nombre completo *", android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        formCard.addView(nameInput);

        pinInput = registerInput("Crea tu PIN *", android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        formCard.addView(pinInput, marginTop(10));

        pinConfirmInput = registerInput("Repite tu PIN *", android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        formCard.addView(pinConfirmInput, marginTop(10));

        LinearLayout note = new LinearLayout(this);
        note.setOrientation(LinearLayout.HORIZONTAL);
        note.setGravity(Gravity.CENTER_VERTICAL);
        note.setPadding(dp(14), dp(12), dp(14), dp(12));
        note.setBackground(roundedStroke(Color.rgb(246, 247, 250), BORDER, 16, 1));
        TextView noteIcon = text("•", 18, Color.rgb(119, 86, 255), true);
        note.addView(noteIcon, new LinearLayout.LayoutParams(dp(18), dp(18)));
        TextView noteText = text("El PIN queda almacenado como hash para mayor seguridad.", 13, TEXT_MUTED, false);
        LinearLayout.LayoutParams noteTextParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        noteTextParams.setMargins(dp(10), 0, 0, 0);
        note.addView(noteText, noteTextParams);
        formCard.addView(note, marginTop(14));

        termsCheckBox = new CheckBox(this);
        termsCheckBox.setText("Acepto los términos y condiciones");
        termsCheckBox.setTextColor(TEXT_MUTED);
        termsCheckBox.setTextSize(14);
        termsCheckBox.setButtonTintList(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{Color.rgb(119, 86, 255), Color.rgb(156, 166, 176)}
        ));
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateRegisterState(false));
        formCard.addView(termsCheckBox, marginTop(14));

        continueButton = primaryButton("Continuar");
        continueButton.setOnClickListener(v -> {
            if (!isRegisterValid()) {
                updateRegisterState(true);
                return;
            }
            registerNameAndPin();
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
        root.setBackgroundColor(Color.WHITE);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root.addView(scroll, fullSize());

        boolean wide = getResources().getConfiguration().screenWidthDp >= 720;
        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(wide ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        shell.setGravity(wide ? Gravity.CENTER_VERTICAL : Gravity.TOP);
        shell.setPadding(
                wide ? dp(56) : dp(22),
                wide ? dp(44) : dp(28),
                wide ? dp(56) : dp(22),
                wide ? dp(40) : dp(28)
        );
        scroll.addView(shell, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        LinearLayout brandPanel = new LinearLayout(this);
        brandPanel.setOrientation(LinearLayout.VERTICAL);
        brandPanel.setPadding(0, 0, wide ? dp(38) : 0, 0);
        brandPanel.setGravity(Gravity.CENTER_VERTICAL);

        TextView eyebrow = text("DIBAYS FARDOS", 12, Color.rgb(126, 132, 145), true);
        eyebrow.setLetterSpacing(0.12f);
        eyebrow.setGravity(Gravity.START);
        brandPanel.addView(eyebrow);

        TextView headline = text("Tu cuenta segura", wide ? 44 : 38, Color.rgb(119, 86, 255), true);
        headline.setLineSpacing(dp(2), 0.95f);
        headline.setGravity(Gravity.START);
        brandPanel.addView(headline, marginTop(10));

        TextView summary = body("Acceso limpio para administrar cuentas, ventas e inventario con estilo bancario.", 16);
        summary.setGravity(Gravity.START);
        brandPanel.addView(summary, marginTop(14));

        brandPanel.addView(featureLine("Registro protegido", "PIN confirmado y datos ordenados"), marginTop(28));
        brandPanel.addView(featureLine("Acceso rapido", "Continua en segundos"), marginTop(10));
        brandPanel.addView(featureLine("Diseño limpio", "Espacio blanco y jerarquía clara"), marginTop(10));

        LoginIllustrationView financePanel = new LoginIllustrationView(this);
        LinearLayout.LayoutParams financeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                wide ? dp(360) : dp(220)
        );
        financeParams.setMargins(0, wide ? dp(30) : dp(24), 0, wide ? dp(12) : dp(0));
        brandPanel.addView(financePanel, financeParams);

        LinearLayout.LayoutParams brandParams = new LinearLayout.LayoutParams(
                wide ? 0 : ViewGroup.LayoutParams.MATCH_PARENT,
                wide ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT,
                wide ? 1.05f : 0
        );
        if (wide) {
            brandParams.setMargins(0, 0, dp(28), 0);
        }
        shell.addView(brandPanel, brandParams);

        LinearLayout loginPanel = new LinearLayout(this);
        loginPanel.setOrientation(LinearLayout.VERTICAL);
        loginPanel.setPadding(dp(24), wide ? dp(26) : dp(18), dp(24), dp(24));
        loginPanel.setBackground(roundedStroke(Color.WHITE, BORDER, 24, 1));

        loginPanel.addView(topBackRow(v -> showWelcomeScreen()));

        TextView title = text("Elige cómo ingresar", wide ? 28 : 26, INK, true);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        loginPanel.addView(title, marginTop(24));

        TextView subtitle = body("Usa tu nombre y PIN para continuar con una verificación segura.", 15);
        loginPanel.addView(subtitle, marginTop(8));

        LinearLayout secureNotice = new LinearLayout(this);
        secureNotice.setOrientation(LinearLayout.HORIZONTAL);
        secureNotice.setGravity(Gravity.CENTER_VERTICAL);
        secureNotice.setPadding(dp(14), dp(13), dp(14), dp(13));
        secureNotice.setBackground(roundedStroke(Color.rgb(246, 247, 250), BORDER, 16, 1));

        TextView secureIcon = text("•", 20, Color.rgb(119, 86, 255), true);
        secureIcon.setGravity(Gravity.CENTER);
        secureIcon.setBackground(rounded(Color.WHITE, 10));
        secureNotice.addView(secureIcon, new LinearLayout.LayoutParams(dp(34), dp(28)));

        TextView secureText = text("Acceso protegido con PIN confirmado y datos guardados en Supabase.", 14, TEXT_MUTED, false);
        LinearLayout.LayoutParams secureTextParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        secureTextParams.setMargins(dp(12), 0, 0, 0);
        secureNotice.addView(secureText, secureTextParams);
        loginPanel.addView(secureNotice, marginTop(22));

        Button pinLogin = primaryButton("Ingresar con PIN");
        pinLogin.setOnClickListener(v -> showRegisterScreen());
        loginPanel.addView(pinLogin, marginTop(24));

        Button googleLogin = outlineButton("Ingresar con Google");
        googleLogin.setOnClickListener(v -> Toast.makeText(this, "Google requiere configurar OAuth en Supabase.", Toast.LENGTH_LONG).show());
        loginPanel.addView(googleLogin, marginTop(12));

        TextView register = text("No tienes una cuenta? Registrate", 14, TEXT_MUTED, false);
        register.setGravity(Gravity.CENTER);
        register.setOnClickListener(v -> showRegisterScreen());
        loginPanel.addView(register, marginTop(20));

        View divider = new View(this);
        divider.setBackgroundColor(BORDER);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1)
        );
        dividerParams.setMargins(0, dp(24), 0, dp(18));
        loginPanel.addView(divider, dividerParams);

        TextView footer = text("Soporte disponible si no puedes acceder.", 13, Color.rgb(142, 151, 160), false);
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
        showAccountsScreen(null);
    }

    private void showAccountsScreen(SupabaseClient.Account createdAccount) {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root.addView(scroll, fullSize());

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(22), dp(28), dp(22), dp(24));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView eyebrow = text("TU CUENTA", 12, Color.rgb(126, 132, 145), true);
        eyebrow.setLetterSpacing(0.12f);
        content.addView(eyebrow, marginTop(8));

        TextView title = text("Tu cuenta segura", 32, Color.rgb(119, 86, 255), true);
        content.addView(title, marginTop(8));
        TextView subtitle = body("Tu perfil principal ya esta listo y sincronizado con Supabase.", 16);
        subtitle.setGravity(Gravity.START);
        content.addView(subtitle, marginTop(10));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(18), dp(18), dp(18), dp(18));
        panel.setBackground(roundedStroke(Color.WHITE, BORDER, 28, 1));
        content.addView(panel, marginTop(24));

        panel.addView(profileSummaryCard(createdAccount));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.VERTICAL);
        actions.setPadding(0, dp(12), 0, 0);
        actions.setBackground(roundedStroke(Color.rgb(248, 249, 251), BORDER, 22, 1));
        actions.addView(settingsRow("Perfil", "user", v -> Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()), marginTop(10));
        actions.addView(settingsRow("Cambiar PIN", "lock", v -> Toast.makeText(this, "Cambiar PIN", Toast.LENGTH_SHORT).show()), marginTop(6));
        actions.addView(settingsRow("Soporte", "chat", v -> Toast.makeText(this, "Soporte en chat", Toast.LENGTH_SHORT).show()), marginTop(6));
        panel.addView(actions, marginTop(18));

        LinearLayout footerActions = new LinearLayout(this);
        footerActions.setOrientation(LinearLayout.VERTICAL);
        footerActions.setPadding(0, dp(18), 0, 0);
        footerActions.addView(primaryButton("Entrar al panel"));
        Button logout = outlineButton("Cerrar sesion");
        logout.setOnClickListener(v -> showLoginScreen());
        footerActions.addView(logout, marginTop(12));
        panel.addView(footerActions, marginTop(18));

        setContentView(root);
    }

    private View profileSummaryCard(SupabaseClient.Account account) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(roundedStroke(Color.WHITE, BORDER, 22, 1));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        FrameLayout avatar = new FrameLayout(this);
        avatar.setBackground(rounded(Color.rgb(242, 237, 255), 999));
        View icon = new ProfileAvatarGlyph(this);
        avatar.addView(icon, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        ));
        header.addView(avatar, new LinearLayout.LayoutParams(dp(56), dp(56)));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams copyParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        copyParams.setMargins(dp(12), 0, 0, 0);
        header.addView(copy, copyParams);

        copy.addView(text(resolveAccountName(account), 18, INK, true));
        copy.addView(text("Cuenta principal de DIBAYS", 13, TEXT_MUTED, false));

        card.addView(header);
        card.addView(featureLine("Estado", "Sincronizada con Supabase"), marginTop(18));
        card.addView(featureLine("Seguridad", "PIN confirmado"), marginTop(10));
        return card;
    }

    private View settingsRow(String label, String iconType, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(4), 0, dp(4));
        row.setOnClickListener(listener);

        FrameLayout iconWrap = new FrameLayout(this);
        iconWrap.setBackground(rounded(Color.rgb(247, 243, 255), 14));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(32), dp(32));

        View icon = new SettingsGlyph(this, iconType);
        iconWrap.addView(icon, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        ));
        row.addView(iconWrap, iconParams);

        TextView text = text(label, 14, PRIMARY, false);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        textParams.setMargins(dp(12), 0, dp(8), 0);
        row.addView(text, textParams);

        TextView arrow = text(">", 20, Color.rgb(176, 176, 182), false);
        arrow.setGravity(Gravity.CENTER);
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(18)));
        return row;
    }

    private View profileActionRow(String label, int color, boolean destructive, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(8), 0, dp(8));
        row.setOnClickListener(listener);

        TextView icon = text(destructive ? "!" : "↩", 17, color, true);
        icon.setGravity(Gravity.CENTER);
        row.addView(icon, new LinearLayout.LayoutParams(dp(20), dp(20)));

        TextView labelView = text(label, 14, color, false);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        labelParams.setMargins(dp(12), 0, 0, 0);
        row.addView(labelView, labelParams);
        return row;
    }


    private static class ProfileAvatarGlyph extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        ProfileAvatarGlyph(android.content.Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float cx = w / 2f;
            float cy = h / 2f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(145, 103, 255));
            canvas.drawCircle(cx, cy - h * 0.12f, w * 0.14f, paint);
            canvas.drawRoundRect(new RectF(cx - w * 0.20f, cy + h * 0.02f, cx + w * 0.20f, cy + h * 0.30f), w * 0.14f, w * 0.14f, paint);
        }
    }

    private static class SettingsGlyph extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final String type;

        SettingsGlyph(android.content.Context context, String type) {
            super(context);
            this.type = type == null ? "user" : type;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float cx = w / 2f;
            float cy = h / 2f;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(2f, w * 0.08f));
            paint.setColor(Color.rgb(145, 103, 255));

            if ("lock".equals(type)) {
                canvas.drawRoundRect(new RectF(w * 0.26f, h * 0.42f, w * 0.74f, h * 0.74f), w * 0.08f, w * 0.08f, paint);
                canvas.drawArc(new RectF(w * 0.34f, h * 0.18f, w * 0.66f, h * 0.50f), 180f, 180f, false, paint);
                return;
            }

            if ("chat".equals(type)) {
                canvas.drawRoundRect(new RectF(w * 0.22f, h * 0.22f, w * 0.78f, h * 0.68f), w * 0.16f, w * 0.16f, paint);
                canvas.drawLine(w * 0.38f, h * 0.68f, w * 0.30f, h * 0.84f, paint);
                return;
            }

            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy - h * 0.14f, w * 0.12f, paint);
            canvas.drawRoundRect(new RectF(cx - w * 0.14f, cy + h * 0.02f, cx + w * 0.14f, cy + h * 0.30f), w * 0.12f, w * 0.12f, paint);
        }
    }

    private static class DashedDividerView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        DashedDividerView(android.content.Context context) {
            super(context);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2f);
            paint.setColor(Color.rgb(214, 214, 219));
            paint.setPathEffect(new android.graphics.DashPathEffect(new float[]{12f, 10f}, 0f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float y = getHeight() / 2f;
            canvas.drawLine(dpStatic(0), y, getWidth(), y, paint);
        }

        private float dpStatic(int value) {
            return value * getResources().getDisplayMetrics().density;
        }
    }

    private LinearLayout featureLine(String title, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        View marker = new View(this);
        marker.setBackground(rounded(Color.rgb(119, 86, 255), 6));
        row.addView(marker, new LinearLayout.LayoutParams(dp(12), dp(12)));

        LinearLayout textBlock = new LinearLayout(this);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        blockParams.setMargins(dp(12), 0, 0, 0);
        row.addView(textBlock, blockParams);

        textBlock.addView(text(title, 15, INK, true));
        textBlock.addView(text(value, 13, TEXT_MUTED, false));
        return row;
    }

    private String resolveAccountName(SupabaseClient.Account account) {
        if (account != null) {
            if (account.name != null && !account.name.trim().isEmpty()) {
                return account.name.trim();
            }
        }
        return "Cuenta principal";
    }

    private String iconGlyph(String iconType) {
        if ("lock".equals(iconType)) {
            return "🔒";
        }
        if ("chat".equals(iconType)) {
            return "💬";
        }
        return "👤";
    }

    private LinearLayout topBackRow(View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("<", 30, PRIMARY, true);
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

    private void updateRegisterState(boolean showErrors) {
        if (nameInput == null || pinInput == null || pinConfirmInput == null || termsCheckBox == null || continueButton == null) {
            return;
        }

        boolean hasName = !nameInput.getText().toString().trim().isEmpty();
        String pin = pinInput.getText().toString().trim();
        String pinConfirm = pinConfirmInput.getText().toString().trim();
        boolean hasPin = pin.length() >= 4;
        boolean pinMatches = hasPin && pin.equals(pinConfirm);
        boolean valid = isRegisterValid();
        nameInput.setBackground(roundedStroke(Color.WHITE, showErrors && !hasName ? ERROR : BORDER, 18, showErrors && !hasName ? 2 : 1));
        pinInput.setBackground(roundedStroke(Color.WHITE, showErrors && !hasPin ? ERROR : BORDER, 18, showErrors && !hasPin ? 2 : 1));
        pinConfirmInput.setBackground(roundedStroke(Color.WHITE, showErrors && !pinMatches ? ERROR : BORDER, 18, showErrors && !pinMatches ? 2 : 1));

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
        return nameInput != null
                && pinInput != null
                && pinConfirmInput != null
                && termsCheckBox != null
                && !nameInput.getText().toString().trim().isEmpty()
                && pinInput.getText().toString().trim().length() >= 4
                && pinInput.getText().toString().trim().equals(pinConfirmInput.getText().toString().trim())
                && termsCheckBox.isChecked();
    }

    private void registerNameAndPin() {
        String name = nameInput.getText().toString().trim();
        String pinHash = hashPin(pinInput.getText().toString().trim());
        continueButton.setEnabled(false);
        continueButton.setText("Creando cuenta...");

        new Thread(() -> {
            try {
                SupabaseClient.Account account = new SupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY)
                        .registerUser(name, pinHash);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cuenta creada", Toast.LENGTH_SHORT).show();
                    showAccountsScreen(account);
                });
            } catch (Exception exception) {
                runOnUiThread(() -> {
                    continueButton.setText("Continuar");
                    updateRegisterState(false);
                    Toast.makeText(this, "No se pudo crear cuenta: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte item : encoded) {
                String value = Integer.toHexString(0xff & item);
                if (value.length() == 1) {
                    hex.append('0');
                }
                hex.append(value);
            }
            return hex.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo proteger el PIN.", exception);
        }
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

    private EditText registerInput(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setTextSize(16);
        input.setSingleLine(true);
        input.setTextColor(PRIMARY);
        input.setHintTextColor(Color.rgb(142, 151, 160));
        input.setInputType(inputType);
        input.setBackground(roundedStroke(Color.WHITE, BORDER, 18, 1));
        input.setPadding(dp(14), 0, dp(14), 0);
        input.setMinHeight(dp(58));
        input.addTextChangedListener(new TextWatcher() {
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
        return input;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setIncludeFontPadding(true);
        view.setLetterSpacing(0.01f);
        if (bold) {
            view.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        } else {
            view.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        }
        return view;
    }

    private Button primaryButton(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(16);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        button.setAllCaps(false);
        button.setBackground(rounded(PRIMARY, 18));
        button.setMinHeight(dp(58));
        button.setPadding(dp(16), 0, dp(16), 0);
        return button;
    }

    private Button outlineButton(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(16);
        button.setTextColor(PRIMARY);
        button.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        button.setAllCaps(false);
        button.setBackground(roundedStroke(Color.WHITE, BORDER, 18, 1));
        button.setMinHeight(dp(58));
        button.setPadding(dp(16), 0, dp(16), 0);
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
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(new RectF(w * 0.08f, h * 0.04f, w * 0.72f, h * 0.96f), 34, 34, paint);
            canvas.drawRoundRect(new RectF(w * 0.58f, h * 0.16f, w * 0.92f, h * 0.84f), 34, 34, paint);
            canvas.drawRoundRect(new RectF(w * 0.78f, h * 0.16f, w * 0.98f, h * 0.84f), 34, 34, paint);

            paint.setColor(Color.rgb(240, 242, 247));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawRoundRect(new RectF(w * 0.08f, h * 0.04f, w * 0.72f, h * 0.96f), 34, 34, paint);
            canvas.drawRoundRect(new RectF(w * 0.58f, h * 0.16f, w * 0.92f, h * 0.84f), 34, 34, paint);
            canvas.drawRoundRect(new RectF(w * 0.78f, h * 0.16f, w * 0.98f, h * 0.84f), 34, 34, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(245, 246, 250));
            canvas.drawRect(w * 0.13f, h * 0.12f, w * 0.67f, h * 0.16f, paint);
            canvas.drawRect(w * 0.13f, h * 0.20f, w * 0.52f, h * 0.23f, paint);
            canvas.drawRect(w * 0.13f, h * 0.29f, w * 0.60f, h * 0.32f, paint);
            canvas.drawRect(w * 0.13f, h * 0.38f, w * 0.48f, h * 0.41f, paint);

            paint.setColor(Color.rgb(119, 86, 255));
            canvas.drawRoundRect(new RectF(w * 0.13f, h * 0.50f, w * 0.58f, h * 0.57f), 16, 16, paint);
            paint.setColor(Color.rgb(231, 224, 255));
            canvas.drawRoundRect(new RectF(w * 0.13f, h * 0.63f, w * 0.45f, h * 0.67f), 10, 10, paint);

            paint.setColor(Color.rgb(229, 229, 235));
            canvas.drawCircle(w * 0.21f, h * 0.76f, w * 0.028f, paint);
            canvas.drawCircle(w * 0.31f, h * 0.76f, w * 0.028f, paint);
            canvas.drawCircle(w * 0.41f, h * 0.76f, w * 0.028f, paint);

            paint.setColor(Color.rgb(245, 246, 250));
            canvas.drawRect(w * 0.62f, h * 0.24f, w * 0.88f, h * 0.28f, paint);
            canvas.drawRect(w * 0.62f, h * 0.34f, w * 0.84f, h * 0.37f, paint);
            canvas.drawRect(w * 0.62f, h * 0.44f, w * 0.82f, h * 0.47f, paint);
            canvas.drawRect(w * 0.62f, h * 0.54f, w * 0.90f, h * 0.57f, paint);
            canvas.drawRoundRect(new RectF(w * 0.62f, h * 0.66f, w * 0.87f, h * 0.74f), 18, 18, paint);

            paint.setColor(Color.rgb(119, 86, 255));
            canvas.drawCircle(w * 0.90f, h * 0.12f, w * 0.04f, paint);
        }
    }

}

