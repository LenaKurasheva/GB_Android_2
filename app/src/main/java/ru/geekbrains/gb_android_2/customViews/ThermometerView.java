package ru.geekbrains.gb_android_2.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import ru.geekbrains.gb_android_2.R;

public class ThermometerView extends View {
    private static final String TAG = "BatteryView";

    // Цвет градусника
    private int thermometerColor = Color.GRAY;
    // Цвет уровня ртути
    public static int levelColor = Color.GREEN;
    // Цвет ртути при нажатии +
    private int levelPressedColor = Color.WHITE;

    // Изображение градусника
    private RectF thermometerRectangle = new RectF();
    // Изображение уровня ртути
    private Rect levelRectangle = new Rect();
    // Изображение головы градусника
    private RectF headRectangle = new RectF();
    private RectF headLevelRectangle = new RectF();

    // "Краска" уровня ртути при касании +
    private Paint levelPressedPaint;
    // "Краска" градусника
    private Paint thermometerPaint;
    // "Краска" ртути
    private Paint levelPaint;

    // Ширина элемента
    private int width = 0;
    // Высота элемента
    private int height = 0;

    // Уровень ртути
    public static int level = 100;
    // касаемся элемента +
    private boolean pressed = false;
    // Слушатель касания +
    private OnClickListener listener;

    // Константы
    // Отступ элементов
    private static int padding = 10;
    // Скругление углов градуника
    private final static int round = 5;
    // Скругление углов градуника
    private final static int headRound = 25;
    public ThermometerView(Context context) {
        super(context);
        init();
    }
    public static int getLevel(){return level;}

    // Вызывается при добавлении элемента в макет
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    public ThermometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    // Вызывается при добавлении элемента в макет с установленными стилями
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    // int defStyleAttr - базовый установленный стиль
    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    // Вызывается при добавлении элемента в макет с установленными стилями
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    // int defStyleAttr - базовый установленный стиль
    // int defStyleRes - ресурс стиля, если он не был определен в предыдущем параметре
    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    // Инициализация атрибутов пользовательского элемента из xml
    private void initAttr(Context context, AttributeSet attrs) {

        // При помощи этого метода получаем доступ к набору атрибутов.
        // На выходе массив со значениями
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView, 0,
                0);

        // Чтобы получить какое-либо значение из этого массива,
        // надо вызвать соответсвующий метод, и передав в этот метод имя ресурса
        // указанного в файле определения атрибутов (attrs.xml)
        thermometerColor = typedArray.getColor(R.styleable.ThermometerView_thermometer_color, Color.GRAY);

        // вторым параметром идет значение по умолчанию, если атрибут не указан в макете,
        // то будет подставлятся эначение по умолчанию.
        levelColor = typedArray.getColor(R.styleable.ThermometerView_level_color, Color.GREEN);
        levelPressedColor = typedArray.getColor(R.styleable.ThermometerView_level_pressed_color, Color.WHITE);

        // Обратите внимание, что первый параметр пишется особым способом
        // через подчерки. первое слово означает имя определения
        // <declare-styleable name="BatteryView">
        // следующее слово имя атрибута
        // <attr name="level" format="integer" />
        level = typedArray.getInteger(R.styleable.ThermometerView_level, 100);
        padding = typedArray.getInteger(R.styleable.ThermometerView_padding, 10);

        // В конце работы дадим сигнал,
        // что нам больше массив со значениями атрибутов не нужен
        // Система в дальнейшем будет переиспользовать этот объект,
        // и мы никогда не получим к нему доступ из этого элемента
        typedArray.recycle();
    }

    // Начальная инициализация полей класса
    private void init(){
        thermometerPaint = new Paint();
        thermometerPaint.setColor(thermometerColor);
        thermometerPaint.setStyle(Paint.Style.FILL);
        levelPaint = new Paint();
        levelPaint.setColor(levelColor);
        levelPaint.setStyle(Paint.Style.FILL);
        // Задать "краску" для нажатия на элемент +
        levelPressedPaint = new Paint();
        levelPressedPaint.setColor(levelPressedColor);
        levelPressedPaint.setStyle(Paint.Style.FILL);
    }

    // Когда система Андроид создает пользовательский экран, то еще неизвестны размеры элемента.
    // Это связанно с тем, что используются расчетные единица измерения,
    // то есть, чтобы элемент выглядил одинаково на разных усройствах,
    // необходимы расчеты размера элемента, в привязке к размеру экрана, его разрешения и плотности пикселей.
    // Этот метод вызывается при необходимости изменения размера элемента
    // Такая необходимость возникает каждый раз при отрисовке экрана.
    // Если нам надо нарисовать свой элемент, то переопределяем этот метод,
    // и задаем новые размеры нашим элементам внутри view


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");

        /*int dpMargin = ScreenHelper.convertDpToPixels(10, getContext());
        Toast.makeText(getContext(), String.valueOf(dpMargin), Toast.LENGTH_SHORT).show();*/

        // Получить реальные ширину и высоту
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();
        // Отрисовка батареи
        thermometerRectangle.set(padding,padding,width-padding,height-padding);
        levelRectangle.set(2 * padding,
                // Координата по у ровна разности максимальной высоты уровня ртути и ткущей высоты уровня ртути
                // + 2 отступа, т.к. верхняя озможная граница уровня ртути находится ниже границы вью на 2 отступа
                (height-height/3) - (int)((height - height/3)*((double)level/(double)100)) + 2*padding,
                width-2*padding,height-(height/3)+2*padding);
        headRectangle.set(0,height-((float)height/3),width,height);
        headLevelRectangle.set(padding,height-(((float)height/3)-padding), width-padding,height-padding);
    }

    // Вызывается, когда надо нарисовать элемент
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");

        canvas.drawRoundRect(thermometerRectangle, headRound, headRound, thermometerPaint);
        canvas.drawRoundRect(headRectangle, headRound, headRound, thermometerPaint);

        // Условие отрисовки, если нажат или нет элемент +
        if (pressed) {
            canvas.drawRect(levelRectangle, levelPressedPaint);
            canvas.drawRoundRect(headLevelRectangle, headRound, headRound, levelPressedPaint);
        } else {
            levelPaint.setColor(levelColor);
            canvas.drawRect(levelRectangle, levelPaint);
            canvas.drawRoundRect(headLevelRectangle, headRound, headRound, levelPaint);
        }
    }

    // Этот метод срабатывает при касании элемента
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent");

        // получить действие, может быть касанием, отпусканием, перемещением...
        int action = event.getAction();

        // Проверка на начало касания (нажали)
        pressed = action == MotionEvent.ACTION_DOWN;
        if(pressed && listener != null){
            // Если слушатель был установлен, то вызовем его метод
            listener.onClick(this);
        }

        // Чтобы перерисовать, надо вызвать этот метод
        invalidate();

        // Касание обработано, вернем true.
        return true;
    }

    // установка слушателя, это обычный слушатель,
    // с каким уже сталкивались при обработке нажатий на кнопки
    @Override
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
    // Методы жизненного цикла, для изучения
    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        Log.d(TAG, "layout");
        super.layout(l, t, r, b);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout");
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        Log.d(TAG, "draw");
        super.draw(canvas);
    }

    @Override
    public void invalidate() {
        Log.d(TAG, "invalidate");
        super.invalidate();
    }

    @Override
    public void requestLayout() {
        Log.d(TAG, "requestLayout");
        super.requestLayout();
    }
}


