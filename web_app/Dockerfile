FROM python:3.8-alpine

WORKDIR /app

COPY ./requirements.txt /app/requirements.txt

COPY . /app

RUN pip install -r requirements.txt

ENTRYPOINT [ "python" ]

CMD ["main.py" ]