<form id="render-form" multi-submit="no-reset">
    <ui-action action="open-dialog" target-id="render-dialog">
        <button>Show Render Form</button>
    </ui-action>
    <dialog id="render-dialog" width="large">
        <title>Render Form</title>
        <body>
        <h5>MessageML</h5>
            <textarea name="messageml" placeholder="Enter your MessageML here.." required="true"></textarea>
        </body>
        <footer>
            <button name="submit" type="action">Render Message</button>
        </footer>
    </dialog>
</form>
