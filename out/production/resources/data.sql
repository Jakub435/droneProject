CREATE OR REPLACE FUNCTION public."setStandardUserRole"()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    VOLATILE
    COST 100
AS $BODY$BEGIN
INSERT INTO public.user_role(user_id,role_id)
VALUES(NEW.id, (select id from public.role where name = 'USER'));
RETURN NEW;
END;$BODY$;

CREATE TRIGGER "setDefaultUserRole"
    AFTER INSERT
    ON public.users
    FOR EACH ROW
    EXECUTE PROCEDURE public."setStandardUserRole"();

INSERT INTO public.permission(
	id, name)
	VALUES (1, 'FIRST_PERMISSION'), (2, 'SECOND_PERMISSION'), (3, 'THIRD_PERMISSION');

INSERT INTO public.role(
	id, name)
	VALUES (1, 'USER'), (2, 'ADMIN');

INSERT INTO public.role_permission(
	role_id, permission_id)
	VALUES (1,1),(2,1), (2,2), (2,3);